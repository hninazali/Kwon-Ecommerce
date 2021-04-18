package ejb.session.stateless;

//import entity.CustomerEntity;
import entity.CreditCardEntity;
import entity.CustomerEntity;
import entity.GroupCartEntity;
import entity.OrderTransactionEntity;
import entity.OrderLineItemEntity;
import entity.StaffEntity;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.enumeration.ShippingStatusEnum;
import util.exception.BundleInsufficientQuantityOnHandException;
import util.exception.BundleNotFoundException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CreditCardNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupCartNotFoundException;
import util.exception.NeedStaffPermissionException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.OrderTransactionAlreadyVoidedRefundedException;
import util.exception.OrderTransactionNotFoundException;
import util.exception.StaffNotFoundException;



@Stateless

public class OrderTransactionSessionBean implements OrderTransactionSessionBeanLocal
{   

    @EJB(name = "GroupCartSessionBeanLocal")
    private GroupCartSessionBeanLocal groupCartSessionBeanLocal;

    @EJB(name = "OrderLineItemSessionBeanLocal")
    private OrderLineItemSessionBeanLocal orderLineItemSessionBeanLocal;

    @EJB(name = "CreditCardSessionBeanLocal")
    private CreditCardSessionBeanLocal creditCardSessionBeanLocal;

    @EJB(name = "BundleEntitySessionBeanLocal")
    private BundleEntitySessionBeanLocal bundleEntitySessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    //@EJB(name = "CustomerEntitySessionBeanLocal")
    //private CustomerEntitySessionBeanLocal customerEntitySessionBeanLocal;
    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;
    @Resource
    private EJBContext eJBContext;
    
    @EJB
    private StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;
    @EJB
    private ProductEntitySessionBeanLocal productSessionBeanLocal;
    
    
    
    public OrderTransactionSessionBean()
    {
    }
    
    @Override
    public void updateShippingStatus(OrderTransactionEntity order) throws OrderTransactionNotFoundException
    {
        OrderTransactionEntity tempOrder = this.retrieveOrderTransactionById(order.getOrderTransactionId());
        tempOrder.setShippingStatus(ShippingStatusEnum.CURRENTLY_SHIPPING);
    }
    
    // Updated in v4.1
    @Override
    public List<OrderTransactionEntity> retrieveOrderTransactionsByCustomer(Long customerId) throws CustomerNotFoundException
    {
        CustomerEntity customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
        customer.getOrderTransactionEntities().size();
        return customer.getOrderTransactionEntities();
    }
    
    @Override
    public OrderTransactionEntity createNewOrderTransaction(Long staffId, OrderTransactionEntity newOrderTransaction) throws StaffNotFoundException, CreateNewOrderTransactionException, BundleNotFoundException, BundleInsufficientQuantityOnHandException
    {
        if(newOrderTransaction != null)
        {
            try
            {
                StaffEntity staffEntity = staffEntitySessionBeanLocal.retrieveStaffByStaffId(staffId);
                newOrderTransaction.setStaffEntity(staffEntity);
                staffEntity.getOrderTransactionEntities().add(newOrderTransaction);

                entityManager.persist(newOrderTransaction);

                for(OrderLineItemEntity orderLineItem:newOrderTransaction.getOrderLineItemEntities())
                {
                    if (orderLineItem.getProductEntity() != null) 
                    {
                        productSessionBeanLocal.debitQuantityOnHand(orderLineItem.getProductEntity().getProductId(), orderLineItem.getQuantity());
                        //entityManager.persist(orderLineItem);
                    }
                    else
                    {
                        bundleEntitySessionBeanLocal.debitQuantityOnHand(orderLineItem.getBundleEntity().getBundleId(), orderLineItem.getQuantity());
                        //entityManager.persist(orderLineItem);
                    }
                }

                //entityManager.flush();

                return newOrderTransaction;
            }
            catch(ProductNotFoundException | ProductInsufficientQuantityOnHandException ex)
            {
                // The line below rolls back all changes made to the database.
                eJBContext.setRollbackOnly();
                throw new CreateNewOrderTransactionException(ex.getMessage());
            }
        }
        else
        {
            throw new CreateNewOrderTransactionException("Order transaction information not provided");
        }
    }
    
    @Override
    public void updateOrderArrived(Long orderId) throws OrderTransactionNotFoundException, NeedStaffPermissionException
    {
        OrderTransactionEntity order = this.retrieveOrderTransactionById(orderId);
        if (order.getShippingStatus() == ShippingStatusEnum.CURRENTLY_SHIPPING)
        {
            order.setShippingStatus(ShippingStatusEnum.DELIVERED);
        }
        else
        {
            throw new NeedStaffPermissionException("The order has not been dispatched by the staff!!!");
        }
    }
    
    @Override
    public OrderTransactionEntity createNewOrderTransactionForCustomer(Long customerId, OrderTransactionEntity newOrderTransaction, Long creditCardId) throws CustomerNotFoundException, CreateNewOrderTransactionException, BundleNotFoundException, BundleInsufficientQuantityOnHandException, ProductNotFoundException, ProductInsufficientQuantityOnHandException, CreditCardNotFoundException
    {
        if(newOrderTransaction != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
                CreditCardEntity ccard = creditCardSessionBeanLocal.retrieveCreditCartById(creditCardId);
                
                //entityManager.persist(newOrderTransaction);
                newOrderTransaction.setCustomerEntity(customer);
                newOrderTransaction.setCardNumber(ccard.getCardNumber());
                customer.getOrderTransactionEntities().add(newOrderTransaction);

                entityManager.persist(newOrderTransaction);

                
                for(OrderLineItemEntity orderLineItemEntity : customer.getPersonalCartEntity().getOrderLineItemEntities())
                {
                    newOrderTransaction.getOrderLineItemEntities().add(orderLineItemEntity);
                    if (orderLineItemEntity.getProductEntity() != null)
                    {
                        productSessionBeanLocal.debitQuantityOnHand(orderLineItemEntity.getProductEntity().getProductId(), orderLineItemEntity.getQuantity());
                    } 
                    else
                    {
                        bundleEntitySessionBeanLocal.debitQuantityOnHand(orderLineItemEntity.getBundleEntity().getBundleId(), orderLineItemEntity.getQuantity());
                    }
                    //orderLineItemEntity.setCustomerEntity(null);
                    //customer.getPersonalCartEntity().getOrderLineItemEntities().remove(orderLineItemEntity);
                }
                

                entityManager.flush();

                return newOrderTransaction;
            }
            catch(ProductNotFoundException | ProductInsufficientQuantityOnHandException ex)
            {
                // The line below rolls back all changes made to the database.
                eJBContext.setRollbackOnly();
                throw new CreateNewOrderTransactionException(ex.getMessage());
            }
        }
        else
        {
            throw new CreateNewOrderTransactionException("Order transaction information not provided");
        }
    }
    
    @Override
    public OrderTransactionEntity createNewOrderTransactionForGroup(Long customerId, OrderTransactionEntity newOrderTransaction, GroupCartEntity groupCart, Long creditCardId) throws GroupCartNotFoundException, CustomerNotFoundException, CreateNewOrderTransactionException, BundleNotFoundException, BundleInsufficientQuantityOnHandException, ProductNotFoundException, CreditCardNotFoundException, ProductInsufficientQuantityOnHandException, OrderLineItemNotFoundException
    {
        if(newOrderTransaction != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
                CreditCardEntity ccard = creditCardSessionBeanLocal.retrieveCreditCartById(creditCardId);
                GroupCartEntity cart = groupCartSessionBeanLocal.retrieveGroupCartById(groupCart.getGroupCartId());
                
                newOrderTransaction.setCustomerEntity(customer);
                newOrderTransaction.setCardNumber(ccard.getCardNumber());
                entityManager.persist(newOrderTransaction);
                
                customer.getOrderTransactionEntities().add(newOrderTransaction);
                System.out.println(cart.getOrderLineItemEntities().size());
                List<OrderLineItemEntity> tempItems = cart.getOrderLineItemEntities();
                int size = cart.getOrderLineItemEntities().size();
                //OrderLineItemEntity orderLineItemEntity = new OrderLineItemEntity();
                //tempItems.addAll(groupCart.getOrderLineItemEntities());

                //entityManager.persist(newOrderTransaction);

                
                for(OrderLineItemEntity orderLineItemEntity : cart.getOrderLineItemEntities())
                {
//                    OrderLineItemEntity temp = tempItems.get(i);
//                    orderLineItemEntity = orderLineItemSessionBeanLocal.retrieveOrderLineItemById(temp.getOrderLineItemId());
//                    System.out.println(orderLineItemEntity.getOrderLineItemId() + "    ******");
                    newOrderTransaction.getOrderLineItemEntities().add(orderLineItemEntity);
                    if (orderLineItemEntity.getProductEntity() != null)
                    {
                        productSessionBeanLocal.debitQuantityOnHand(orderLineItemEntity.getProductEntity().getProductId(), orderLineItemEntity.getQuantity());
                    } 
                    else
                    {
                        bundleEntitySessionBeanLocal.debitQuantityOnHand(orderLineItemEntity.getBundleEntity().getBundleId(), orderLineItemEntity.getQuantity());
                    }
                    //orderLineItemEntity.setCustomerEntity(null);
                    //customer.getPersonalCartEntity().getOrderLineItemEntities().remove(orderLineItemEntity);
                    
                }
                groupCart.getOrderLineItemEntities().clear();
                

                entityManager.flush();

                return newOrderTransaction;
            }
            catch(ProductNotFoundException | ProductInsufficientQuantityOnHandException ex)
            {
                // The line below rolls back all changes made to the database.
                eJBContext.setRollbackOnly();
                throw new CreateNewOrderTransactionException(ex.getMessage());
            }
        }
        else
        {
            throw new CreateNewOrderTransactionException("Order transaction information not provided");
        }
    }
    
    
    
    @Override
    public List<OrderTransactionEntity> retrieveAllOrderTransactions()
    {
        Query query = entityManager.createQuery("SELECT st FROM OrderTransactionEntity st");
        
        List<OrderTransactionEntity> orderTransactions = query.getResultList();
        
        for (OrderTransactionEntity temp : orderTransactions)
        {
            temp.getOrderLineItemEntities().size();
        }
        
        return orderTransactions;
    }
    
    
    
    // Added in v4.1
    
    @Override
    public List<OrderLineItemEntity> retrieveOrderLineItemsByProductId(Long productId)
    {
        Query query = entityManager.createNamedQuery("selectAllOrderTransactionLineItemsByProductId");
        query.setParameter("inProductId", productId);
        
        return query.getResultList();
    }
    
    
    
    @Override
    public OrderTransactionEntity retrieveOrderTransactionById(Long orderTransactionId) throws OrderTransactionNotFoundException
    {
        OrderTransactionEntity orderTransactionEntity = entityManager.find(OrderTransactionEntity.class, orderTransactionId);
        
        if(orderTransactionEntity != null)
        {
            for(OrderLineItemEntity orderLineItem:orderTransactionEntity.getOrderLineItemEntities())
            {
                orderLineItem.getProductEntity();
            }
            
            return orderTransactionEntity;
        }
        else
        {
            throw new OrderTransactionNotFoundException("Order Transaction ID " + orderTransactionId + " does not exist!");
        }                
    }
    
    
    
    @Override
    public void updateOrderTransaction(OrderTransactionEntity orderTransactionEntity)
    {
        entityManager.merge(orderTransactionEntity);
    }
    
    
    
    // Updated in v4.1
    
    @Override
    public void voidRefundOrderTransaction(Long orderTransactionId) throws OrderTransactionNotFoundException, OrderTransactionAlreadyVoidedRefundedException
    {
        OrderTransactionEntity orderTransactionEntity = retrieveOrderTransactionById(orderTransactionId);
        
        if(!orderTransactionEntity.getVoidRefund())
        {
            for(OrderLineItemEntity orderLineItem:orderTransactionEntity.getOrderLineItemEntities())
            {
                if (orderLineItem.getProductEntity() != null)
                {
                    try
                    {
                        productSessionBeanLocal.creditQuantityOnHand(orderLineItem.getProductEntity().getProductId(), orderLineItem.getQuantity());
                    }
                    catch(ProductNotFoundException ex)
                    {
                        ex.printStackTrace(); // Ignore exception since this should not happen
                    }    
                }
                else
                {
                   try
                    {
                        bundleEntitySessionBeanLocal.creditQuantityOnHand(orderLineItem.getBundleEntity().getBundleId(), orderLineItem.getQuantity());
                    }
                    catch(BundleNotFoundException | ProductNotFoundException ex)
                    {
                        ex.printStackTrace(); // Ignore exception since this should not happen
                    }    
                }
            }
            
            orderTransactionEntity.setVoidRefund(true);
        }
        else
        {
            throw new OrderTransactionAlreadyVoidedRefundedException("The order transaction has aready been voided/refunded");
        }
    }
    
    
    
    @Override
    public void deleteOrderTransaction(OrderTransactionEntity orderTransactionEntity)
    {
        throw new UnsupportedOperationException();
    }
    
    /*
    @Override
    public OrderTransactionEntity createNewOrderTransactionForCheckout(Long customerId, OrderTransactionEntity newOrderTransaction) throws CustomerNotFoundException, CreateNewOrderTransactionException
    {
        if(newOrderTransaction != null)
        {
            try
            {
                CustomerEntity customerEntity = customerEntitySessionBeanLocal.retrieveCustomerByCustomerId(customerId);
                newOrderTransaction.setCustomerEntity(customerEntity);
                customerEntity.getOrderTransactionEntities().add(newOrderTransaction);

                entityManager.persist(newOrderTransaction);

                for(OrderTransactionLineItemEntity orderLineItem:newOrderTransaction.getOrderTransactionLineItemEntities())
                {
                    productSessionBeanLocal.debitQuantityOnHand(orderLineItem.getProductEntity().getProductId(), orderLineItem.getQuantity());
                    entityManager.persist(orderLineItem);
                }

                entityManager.flush();

                return newOrderTransaction;
            }
            catch(ProductNotFoundException | ProductInsufficientQuantityOnHandException ex)
            {
                // The line below rolls back all changes made to the database.
                eJBContext.setRollbackOnly();
                throw new CreateNewOrderTransactionException(ex.getMessage());
            }
        }
        else
        {
            throw new CreateNewSaleTransactionException("Sale transaction information not provided");
        }
    }
    */
    
    @Override
    public boolean refundOrder(Long customerId, Long orderTransactionId) throws OrderTransactionNotFoundException, OrderTransactionAlreadyVoidedRefundedException, CustomerNotFoundException
    {
        CustomerEntity customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
        OrderTransactionEntity orderTransaction = retrieveOrderTransactionById(orderTransactionId);
        
        if (!customer.getUsername().equals(orderTransaction.getCustomerEntity().getUsername()))
        {
            return false;
        }
        //StaffEntity staff = staffEntitySessionBeanLocal.retrieveStaffByStaffId(staffId);
        Date oldDate = orderTransaction.getTransactionDateTime();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        Calendar c = Calendar.getInstance();
        
        //Setting the date to the given date
        c.setTime(oldDate);
        
        LocalDate localOldDate = convertToLocalDateViaInstant(oldDate);
        LocalDate localNewDate = localOldDate.plusDays(14);
        LocalDate nowDate = LocalDate.now();
        if (nowDate.isBefore(localNewDate))
        {
            this.voidRefundOrderTransaction(orderTransaction.getOrderTransactionId());
            return true;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public void updateShippingStatus(long orderId, ShippingStatusEnum shippingStatus) throws OrderTransactionNotFoundException
    {
        OrderTransactionEntity order = retrieveOrderTransactionById(orderId);
        order.setShippingStatus(shippingStatus);
    }
    
    /*
    @Override
    public void rejectRefund(OrderTransactionEntity orderTransaction) throws OrderTransactionAlreadyVoidedRefundedException
    {
        
    }
    */
    
    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
