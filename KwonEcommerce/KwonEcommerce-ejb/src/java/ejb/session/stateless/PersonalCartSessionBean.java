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
import entity.PersonalCartEntity;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import util.exception.CreateNewPersonalCartException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupCartNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.PersonalCartNotFoundException;

@Stateless
public class PersonalCartSessionBean implements PersonalCartSessionBeanLocal {

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

    public PersonalCartSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public PersonalCartEntity createPersonalCartEntity(PersonalCartEntity newPersonalCartEntity) throws InputDataValidationException, CreateNewPersonalCartException {
        Set<ConstraintViolation<PersonalCartEntity>> constraintViolations = validator.validate(newPersonalCartEntity);

        if (constraintViolations.isEmpty()) {
            try {

                entityManager.persist(newPersonalCartEntity);
                entityManager.flush();
                System.out.println("create personal cart");
                return newPersonalCartEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null
                        && ex.getCause().getCause() != null
                        && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new CreateNewPersonalCartException("Personal cart with the same ID already exist");
                } else {
                    throw new CreateNewPersonalCartException("An unexpected error has occurred: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw new CreateNewPersonalCartException("An unexpected error has occurred: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<PersonalCartEntity> retrieveAllPersonalCartEntities() {
        Query query = entityManager.createQuery("SELECT pc FROM PersonalCartEntity pc");

        List<PersonalCartEntity> personalCartEntities = query.getResultList();
        for (PersonalCartEntity pc : personalCartEntities) {
            pc.getOrderLineItemEntities();
        }

        return personalCartEntities;
    }

    @Override
    public PersonalCartEntity retrievePersonalCartById(Long personalCartId) throws PersonalCartNotFoundException {
        PersonalCartEntity personalCartEntity = entityManager.find(PersonalCartEntity.class, personalCartId);

        if (personalCartEntity != null) {
            return personalCartEntity;
        } else {
            throw new PersonalCartNotFoundException("Personal Cart ID " + personalCartId + " does not exist!");
        }
    }

    @Override
    public void addOrderLineItemToCart(Long personalCartId, OrderLineItemEntity orderLineItemEntity) throws PersonalCartNotFoundException {
        PersonalCartEntity personalCartEntity = retrievePersonalCartById(personalCartId);
        personalCartEntity.getOrderLineItemEntities().add(orderLineItemEntity);
    }

    @Override
    public void checkOutCart(Long customerId, Long personalCartId) throws PersonalCartNotFoundException, CustomerNotFoundException {
        PersonalCartEntity personalCartEntity = retrievePersonalCartById(personalCartId);
        CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerById(customerId);
        Integer totalQty = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        Integer totalLineItem = personalCartEntity.getOrderLineItemEntities().size();
        for (OrderLineItemEntity ol : personalCartEntity.getOrderLineItemEntities()) {
            Integer itemQuantity = ol.getQuantity();
            totalQty += itemQuantity;
            BigDecimal price = ol.getUnitPrice();
            totalAmount.add(price.multiply(BigDecimal.valueOf(itemQuantity)));
        }

        OrderTransactionEntity orderTransactionEntity = new OrderTransactionEntity(totalLineItem, totalQty, totalAmount, new Date(), false);
        orderTransactionEntity.getCustomerEntities().add(customerEntity);
        clearPersonalCart(personalCartEntity);
    }

    public void clearPersonalCart(PersonalCartEntity personalCartEntity) {
        personalCartEntity.getOrderLineItemEntities().clear();

    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<PersonalCartEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}