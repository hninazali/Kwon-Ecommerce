/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BundleEntity;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import entity.PersonalCartEntity;
import entity.ProductEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CreateNewPersonalCartException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.PersonalCartNotFoundException;

@Local
public interface PersonalCartSessionBeanLocal {

    PersonalCartEntity createPersonalCartEntity(PersonalCartEntity newPersonalCartEntity) throws InputDataValidationException, CreateNewPersonalCartException;

    List<PersonalCartEntity> retrieveAllPersonalCartEntities();

    PersonalCartEntity retrievePersonalCartById(Long personalCartId) throws PersonalCartNotFoundException;

    void addOrderLineItemToCart(Long personalCartId, OrderLineItemEntity orderLineItemEntity) throws PersonalCartNotFoundException;

    public OrderTransactionEntity checkOutCart(Long customerId, Long personalCartId) throws PersonalCartNotFoundException, CustomerNotFoundException, CreateNewOrderTransactionException;

    public PersonalCartEntity retrievePersonalCartEntity(Long customerId) throws CustomerNotFoundException;

    public OrderLineItemEntity addNewOrderLineItemToCart(Long customerId, OrderLineItemEntity orderLineItemEntity) throws CreateNewOrderLineItemException, CustomerNotFoundException, InputDataValidationException;

    public OrderLineItemEntity updateOrderLineItem(Long customerId, OrderLineItemEntity orderLineItemEntity, Integer newQuantity) throws CustomerNotFoundException, OrderLineItemNotFoundException;

    public void removeOrderLineItem(Long customerId, Long lineItemId) throws CustomerNotFoundException, OrderLineItemNotFoundException;

    public PersonalCartEntity retrievePersonalCartByIdEager(Long personalCartId) throws PersonalCartNotFoundException;

    public boolean isInsideCart(Long customerId, ProductEntity product) throws CustomerNotFoundException;

    public OrderLineItemEntity addQuantity(Long customerId, ProductEntity product, Integer addedQty) throws CustomerNotFoundException;

    public boolean bundleIsInsideCart(Long customerId, BundleEntity bundle) throws CustomerNotFoundException;

    public OrderLineItemEntity addQuantityBundle(Long customerId, BundleEntity bundle, Integer addedQty) throws CustomerNotFoundException;
}
