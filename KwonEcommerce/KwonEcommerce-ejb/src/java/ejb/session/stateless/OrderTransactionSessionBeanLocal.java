/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.GroupCartEntity;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.ShippingStatusEnum;
import util.exception.BundleInsufficientQuantityOnHandException;
import util.exception.BundleNotFoundException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CreditCardNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupCartNotFoundException;
import util.exception.NeedStaffPermissionException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.OrderTransactionAlreadyVoidedRefundedException;
import util.exception.OrderTransactionNotFoundException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.StaffNotFoundException;

/**
 *
 * @author User
 */
@Local
public interface OrderTransactionSessionBeanLocal {

    public OrderTransactionEntity createNewOrderTransaction(Long staffId, OrderTransactionEntity newOrderTransaction) throws StaffNotFoundException, CreateNewOrderTransactionException, BundleNotFoundException, BundleInsufficientQuantityOnHandException;

    public List<OrderTransactionEntity> retrieveAllOrderTransactions();

    public void updateOrderTransaction(OrderTransactionEntity orderTransactionEntity);

    public void voidRefundOrderTransaction(Long orderTransactionId) throws OrderTransactionNotFoundException, OrderTransactionAlreadyVoidedRefundedException;

    public void deleteOrderTransaction(OrderTransactionEntity orderTransactionEntity);

    public OrderTransactionEntity retrieveOrderTransactionById(Long orderTransactionId) throws OrderTransactionNotFoundException;

    public boolean refundOrder(Long customerId, Long orderTransactionId) throws OrderTransactionNotFoundException, OrderTransactionAlreadyVoidedRefundedException, CustomerNotFoundException;

    public void updateShippingStatus(long orderId, ShippingStatusEnum shippingStatus) throws OrderTransactionNotFoundException;

    public List<OrderLineItemEntity> retrieveOrderLineItemsByProductId(Long productId);

    public OrderTransactionEntity createNewOrderTransactionForCustomer(Long customerId, OrderTransactionEntity newOrderTransaction, Long creditCardId) throws CustomerNotFoundException, CreateNewOrderTransactionException, BundleNotFoundException, BundleInsufficientQuantityOnHandException, ProductNotFoundException, ProductInsufficientQuantityOnHandException, CreditCardNotFoundException;

    public List<OrderTransactionEntity> retrieveOrderTransactionsByCustomer(Long customerId) throws CustomerNotFoundException;

    public OrderTransactionEntity createNewOrderTransactionForGroup(Long customerId, OrderTransactionEntity newOrderTransaction, GroupCartEntity groupCart, Long creditCardId) throws GroupCartNotFoundException, CustomerNotFoundException, CreateNewOrderTransactionException, BundleNotFoundException, BundleInsufficientQuantityOnHandException, ProductNotFoundException, CreditCardNotFoundException, ProductInsufficientQuantityOnHandException, OrderLineItemNotFoundException;

    public void updateShippingStatus(OrderTransactionEntity order) throws OrderTransactionNotFoundException;

    public void updateOrderArrived(Long orderId) throws OrderTransactionNotFoundException, NeedStaffPermissionException;
    
}
