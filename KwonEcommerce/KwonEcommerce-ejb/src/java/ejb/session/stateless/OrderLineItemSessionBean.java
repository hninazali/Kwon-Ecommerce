/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BundleEntity;
import entity.CustomerEntity;
import entity.GroupCartEntity;
import entity.OrderLineItemEntity;
import entity.ProductEntity;
import java.math.BigDecimal;
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
import util.exception.BundleNotFoundException;
import util.exception.CreateNewGroupCartException;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.ProductNotFoundException;

@Stateless
public class OrderLineItemSessionBean implements OrderLineItemSessionBeanLocal {

    @EJB(name = "BundleEntitySessionBeanLocal")
    private BundleEntitySessionBeanLocal bundleEntitySessionBeanLocal;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @EJB
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @EJB
    private ProductEntitySessionBeanLocal productSessionBeanLocal;

    public OrderLineItemSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public OrderLineItemEntity createNewOrderLineItem(Long customerId, OrderLineItemEntity newOrderLineItem) throws CreateNewOrderLineItemException, CustomerNotFoundException, InputDataValidationException {
        
        Set<ConstraintViolation<OrderLineItemEntity>> constraintViolations = validator.validate(newOrderLineItem);
                
        if (newOrderLineItem != null) {
            try {
                CustomerEntity customerEntity = customerSessionBeanLocal.retrieveCustomerById(customerId);
                newOrderLineItem.setCustomerEntity(customerEntity);
                customerEntity.getOrderLineItemEntities().add(newOrderLineItem);
                entityManager.persist(newOrderLineItem);
                entityManager.flush();

                return newOrderLineItem;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null
                        && ex.getCause().getCause() != null
                        && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new CreateNewOrderLineItemException("Order line item with the same ID already exists.");
                } else {
                    throw new CreateNewOrderLineItemException("An unexpected error has occurred: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw new CreateNewOrderLineItemException("An unexpected error has occurred: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public OrderLineItemEntity createLineItemForCart(Long productId, Integer quantity) throws ProductNotFoundException
    {
        ProductEntity product = productSessionBeanLocal.retrieveProductByProductId(productId);
        OrderLineItemEntity lineItem = new OrderLineItemEntity(product, quantity, product.getUnitPrice(), product.getUnitPrice().multiply(new BigDecimal(quantity)));
        return lineItem;
    }
    
    @Override
    public OrderLineItemEntity createLineItemForCartBundle(Long bundleId, Integer quantity) throws BundleNotFoundException
    {
        BundleEntity bundle = bundleEntitySessionBeanLocal.retrieveBundleByBundleId(bundleId);
        OrderLineItemEntity lineItem = new OrderLineItemEntity(bundle, quantity, bundle.getUnitPrice(), bundle.getUnitPrice().multiply(new BigDecimal(quantity)));
        return lineItem;
    }

    @Override
    public List<OrderLineItemEntity> retrieveAllOrderLineItemEntities() {
        Query query = entityManager.createQuery("SELECT ol FROM OrderLineItemEntity ol");

        List<OrderLineItemEntity> orderLineItems = query.getResultList();
        
        return orderLineItems;
    }

    @Override
    public OrderLineItemEntity retrieveOrderLineItemById(Long orderLineItemId) throws OrderLineItemNotFoundException {
        OrderLineItemEntity orderLineItem = entityManager.find(OrderLineItemEntity.class, orderLineItemId);

        if (orderLineItem != null) {
            return orderLineItem;
        } else {
            throw new OrderLineItemNotFoundException("Order Line Item ID " + orderLineItemId + " does not exist!");
        }
    }

    @Override
    public void updateOrderLineItemEntity(OrderLineItemEntity orderLineItemEntity) {
        entityManager.merge(orderLineItemEntity);
    }
    
    @Override
    public OrderLineItemEntity updateOrderLineItemEntityQty(Long lineItemId, Integer quantity) throws OrderLineItemNotFoundException {
        OrderLineItemEntity lineItem = this.retrieveOrderLineItemById(lineItemId);
        
        lineItem.setQuantity(quantity);
        if (lineItem.getProductEntity() != null)
        {
            ProductEntity product = lineItem.getProductEntity();
            BigDecimal price = product.getUnitPrice();
            BigDecimal newPrice = price.multiply(new BigDecimal(lineItem.getQuantity()));
            lineItem.setSubTotal(newPrice);
        }
        else
        {
            BundleEntity bundle = lineItem.getBundleEntity();
            BigDecimal price2 = bundle.getUnitPrice();
            BigDecimal newPrice2 = price2.multiply(new BigDecimal(lineItem.getQuantity()));
            lineItem.setSubTotal(newPrice2);
        }
        
        return lineItem;
    }

    @Override
    public void deleteOrderLineItemEntity(OrderLineItemEntity orderLineItemEntity) {
        throw new UnsupportedOperationException();
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<OrderLineItemEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
