/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.GroupCartEntity;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateNewGroupCartException;
import util.exception.CustomerExistInCartException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupCartNotFoundException;
import util.exception.InputDataValidationException;

@Stateless
public class GroupCartSessionBean implements GroupCartSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;

    @EJB
    private OrderTransactionSessionBeanLocal orderTransactionSessionBeanLocal;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public GroupCartSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public GroupCartEntity createNewGroupCart(GroupCartEntity newGroupCartEntity) throws InputDataValidationException, CreateNewGroupCartException {
        Set<ConstraintViolation<GroupCartEntity>> constraintViolations = validator.validate(newGroupCartEntity);

        if (constraintViolations.isEmpty()) {
            try {

                CustomerEntity owner = newGroupCartEntity.getGroupOwner();
                owner.getGroupCartEntities().add(newGroupCartEntity);
                newGroupCartEntity.getCustomerEntities().add(owner);
                entityManager.persist(newGroupCartEntity);
                entityManager.flush();

                return newGroupCartEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null
                        && ex.getCause().getCause() != null
                        && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new CreateNewGroupCartException("Group cart with the same ID already exist");
                } else {
                    throw new CreateNewGroupCartException("An unexpected error has occurred: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw new CreateNewGroupCartException("An unexpected error has occurred: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<GroupCartEntity> retrieveAllGroupCartEntities() {
        Query query = entityManager.createQuery("SELECT gc FROM GroupCartEntity gc");

        List<GroupCartEntity> groupCartEntities = query.getResultList();
        for(GroupCartEntity gc : groupCartEntities)
        {
            gc.getCustomerEntities().size();
            gc.getOrderLineItemEntities().size();
        }
        
        return groupCartEntities;
    }

    @Override
    public GroupCartEntity retrieveGroupCartById(Long groupCartId) throws GroupCartNotFoundException {
        GroupCartEntity groupCartEntity = entityManager.find(GroupCartEntity.class, groupCartId);

        if (groupCartEntity != null) {
            return groupCartEntity;
        } else {
            throw new GroupCartNotFoundException("Group Cart ID " + groupCartId + " does not exist!");
        }
    }

    @Override
    public void addMemberToCart(Long groupCartId, Long customerEntityId) throws CustomerNotFoundException, GroupCartNotFoundException, CustomerExistInCartException {
        CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerById(customerEntityId);
        GroupCartEntity groupCartEntity = retrieveGroupCartById(groupCartId);

        if (groupCartEntity.getCustomerEntities().contains(customerEntity)) {
            throw new CustomerExistInCartException("Customer " + customerEntity.getFirstName() + " " + customerEntity.getLastName() + " is already a member of this group cart!");
        } else {
            groupCartEntity.getCustomerEntities().add(customerEntity);
            customerEntity.getGroupCartEntities().add(groupCartEntity);
        }
    }

    @Override
    public void addOrderLineItemToCart(Long groupCartId, OrderLineItemEntity orderLineItemEntity) throws GroupCartNotFoundException {
        GroupCartEntity groupCartEntity = retrieveGroupCartById(groupCartId);
        groupCartEntity.getOrderLineItemEntities().add(orderLineItemEntity);
    }

    @Override
    public void checkOutCart(Long groupCartId) throws GroupCartNotFoundException {
        GroupCartEntity groupCartEntity = retrieveGroupCartById(groupCartId);
        Integer totalQty = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        Integer totalLineItem = groupCartEntity.getOrderLineItemEntities().size();
        for (OrderLineItemEntity ol : groupCartEntity.getOrderLineItemEntities()) {
            Integer itemQuantity = ol.getQuantity();
            totalQty += itemQuantity;
            BigDecimal price = ol.getUnitPrice();
            totalAmount.add(price.multiply(BigDecimal.valueOf(itemQuantity)));
        }

        OrderTransactionEntity orderTransactionEntity = new OrderTransactionEntity(totalLineItem, totalQty, totalAmount, new Date(), false);
        orderTransactionEntity.setCustomerEntity(groupCartEntity.getCustomerEntities());
        clearGroupCart(groupCartEntity);
    }

    public void clearGroupCart(GroupCartEntity groupCartEntity) {
        groupCartEntity.getOrderLineItemEntities().clear();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<GroupCartEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
