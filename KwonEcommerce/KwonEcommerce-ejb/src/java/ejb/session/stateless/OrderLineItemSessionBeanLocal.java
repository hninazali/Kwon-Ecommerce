/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OrderLineItemEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.ProductNotFoundException;

/**
 *
 * @author User
 */
@Local
public interface OrderLineItemSessionBeanLocal {

    OrderLineItemEntity createNewOrderLineItem(Long customerId, OrderLineItemEntity newOrderLineItem) throws CreateNewOrderLineItemException, CustomerNotFoundException, InputDataValidationException;

    List<OrderLineItemEntity> retrieveAllOrderLineItemEntities();

    OrderLineItemEntity retrieveOrderLineItemById(Long orderLineItemId) throws OrderLineItemNotFoundException;

    void updateOrderLineItemEntity(OrderLineItemEntity orderLineItemEntity);

    void deleteOrderLineItemEntity(OrderLineItemEntity orderLineItemEntity);

    public OrderLineItemEntity updateOrderLineItemEntityQty(Long lineItemId, Integer quantity) throws OrderLineItemNotFoundException;

    public OrderLineItemEntity createLineItemForCart(Long productId, Integer quantity) throws ProductNotFoundException;
}
