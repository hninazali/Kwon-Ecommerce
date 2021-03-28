/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.ShippingStatusEnum;
import util.exception.CreateNewOrderTransactionException;
import util.exception.OrderTransactionAlreadyVoidedRefundedException;
import util.exception.OrderTransactionNotFoundException;
import util.exception.StaffNotFoundException;

/**
 *
 * @author User
 */
@Local
public interface OrderTransactionSessionBeanLocal {

    public OrderTransactionEntity createNewOrderTransaction(Long staffId, OrderTransactionEntity newOrderTransaction) throws StaffNotFoundException, CreateNewOrderTransactionException;

    public List<OrderTransactionEntity> retrieveAllOrderTransactions();

    public void updateOrderTransaction(OrderTransactionEntity orderTransactionEntity);

    public void voidRefundOrderTransaction(Long orderTransactionId) throws OrderTransactionNotFoundException, OrderTransactionAlreadyVoidedRefundedException;

    public void deleteOrderTransaction(OrderTransactionEntity orderTransactionEntity);

    public OrderTransactionEntity retrieveOrderTransactionById(Long orderTransactionId) throws OrderTransactionNotFoundException;

    public boolean refundOrder(long orderTransactionId) throws OrderTransactionNotFoundException, OrderTransactionAlreadyVoidedRefundedException;

    public void approveRefund(OrderTransactionEntity orderTransaction) throws OrderTransactionAlreadyVoidedRefundedException;

    public void updateShippingStatus(long orderId, ShippingStatusEnum shippingStatus) throws OrderTransactionNotFoundException;

    public List<OrderLineItemEntity> retrieveOrderLineItemsByProductId(Long productId);
    
}
