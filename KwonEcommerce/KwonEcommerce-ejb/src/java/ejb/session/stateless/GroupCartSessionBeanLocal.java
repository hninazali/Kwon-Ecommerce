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
import util.exception.CreateNewGroupCartException;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CustomerExistInCartException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupCartNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.OrderLineItemNotFoundException;

@Local
public interface GroupCartSessionBeanLocal {

    GroupCartEntity createNewGroupCart(GroupCartEntity newGroupCartEntity) throws InputDataValidationException, CreateNewGroupCartException;

    List<GroupCartEntity> retrieveAllGroupCartEntities();

    GroupCartEntity retrieveGroupCartById(Long groupCartId) throws GroupCartNotFoundException;

    void addMemberToCart(Long groupCartId, Long customerEntityId) throws CustomerNotFoundException, GroupCartNotFoundException, CustomerExistInCartException;

    void addOrderLineItemToCart(Long groupCartId, OrderLineItemEntity orderLineItemEntity) throws GroupCartNotFoundException;

    public List<GroupCartEntity> retrieveCustomerGroupCartEntities(Long customerId) throws CustomerNotFoundException;

    public OrderLineItemEntity addNewOrderLineItemToCart(Long customerId, Long groupCartId, OrderLineItemEntity orderLineItemEntity) throws CreateNewOrderLineItemException, CustomerNotFoundException, InputDataValidationException, GroupCartNotFoundException;

    public OrderLineItemEntity updateOrderLineItem(Long customerId, OrderLineItemEntity orderLineItemEntity, Integer newQuantity) throws CustomerNotFoundException, OrderLineItemNotFoundException;

    public void removeOrderLineItem(Long customerId, Long groupCartId, OrderLineItemEntity orderLineItemEntity) throws CustomerNotFoundException, OrderLineItemNotFoundException, GroupCartNotFoundException;

    public GroupCartEntity retrieveGroupCartByIdEager(Long groupCartId) throws GroupCartNotFoundException;

    public OrderTransactionEntity checkOutCart(Long customerId, Long groupCartId) throws GroupCartNotFoundException, CustomerNotFoundException, CreateNewOrderTransactionException;

}
