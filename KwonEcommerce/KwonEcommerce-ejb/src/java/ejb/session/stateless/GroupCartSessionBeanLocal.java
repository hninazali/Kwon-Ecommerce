/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BundleEntity;
import entity.GroupCartEntity;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import entity.ProductEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.BundleInsufficientQuantityOnHandException;
import util.exception.BundleNotFoundException;
import util.exception.CreateNewGroupCartException;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CustomerExistInCartException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupActivityDetectedException;
import util.exception.GroupCartNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;

@Local
public interface GroupCartSessionBeanLocal {

    public GroupCartEntity createNewGroupCart(Long ownerId, String groupName, List<String> usernames) throws CreateNewGroupCartException;

    List<GroupCartEntity> retrieveAllGroupCartEntities();

    GroupCartEntity retrieveGroupCartById(Long groupCartId) throws GroupCartNotFoundException;

    void addMemberToCart(Long groupCartId, Long customerEntityId) throws CustomerNotFoundException, GroupCartNotFoundException, CustomerExistInCartException;

    void addOrderLineItemToCart(Long groupCartId, OrderLineItemEntity orderLineItemEntity) throws GroupCartNotFoundException;

    public List<GroupCartEntity> retrieveCustomerGroupCartEntities(Long customerId) throws CustomerNotFoundException;

    public OrderLineItemEntity addNewOrderLineItemToCart(Long customerId, Long groupCartId, OrderLineItemEntity orderLineItemEntity) throws CreateNewOrderLineItemException, CustomerNotFoundException, InputDataValidationException, GroupCartNotFoundException;

    public OrderLineItemEntity updateOrderLineItem(Long customerId, OrderLineItemEntity orderLineItemEntity, Integer newQuantity) throws CustomerNotFoundException, OrderLineItemNotFoundException;

    public void removeOrderLineItem(Long customerId, Long groupCartId, OrderLineItemEntity orderLineItemEntity) throws CustomerNotFoundException, OrderLineItemNotFoundException, GroupCartNotFoundException;

    public GroupCartEntity retrieveGroupCartByIdEager(Long groupCartId) throws GroupCartNotFoundException;

    public OrderTransactionEntity checkOutCart(Long customerId, Long groupCartId) throws GroupCartNotFoundException, CustomerNotFoundException, CreateNewOrderTransactionException, BundleNotFoundException, BundleInsufficientQuantityOnHandException, ProductNotFoundException, ProductInsufficientQuantityOnHandException;

    public boolean isInsideCart(Long groupCartId, Long customerId, ProductEntity product) throws CustomerNotFoundException, GroupCartNotFoundException;

    public OrderLineItemEntity addQuantity(Long groupCartId, Long customerId, ProductEntity product, Integer addedQty) throws CustomerNotFoundException, GroupCartNotFoundException;

    public void leaveGroup(Long groupCartId, Long customerId) throws GroupCartNotFoundException, CustomerNotFoundException, GroupActivityDetectedException;

    public boolean bundleIsInsideCart(Long groupCartId, Long customerId, BundleEntity bundle) throws CustomerNotFoundException, GroupCartNotFoundException;

    public OrderLineItemEntity addQuantityBundle(Long groupCartId, Long customerId, BundleEntity bundle, Integer addedQty) throws CustomerNotFoundException, GroupCartNotFoundException;

}
