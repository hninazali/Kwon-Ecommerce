/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.GroupCartEntity;
import entity.OrderLineItemEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewGroupCartException;
import util.exception.CustomerExistInCartException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupCartNotFoundException;
import util.exception.InputDataValidationException;

@Local
public interface GroupCartSessionBeanLocal {

    GroupCartEntity createNewGroupCart(GroupCartEntity newGroupCartEntity) throws InputDataValidationException, CreateNewGroupCartException;

    List<GroupCartEntity> retrieveAllGroupCartEntities();

    GroupCartEntity retrieveGroupCartById(Long groupCartId) throws GroupCartNotFoundException;

    void addMemberToCart(Long groupCartId, Long customerEntityId) throws CustomerNotFoundException, GroupCartNotFoundException, CustomerExistInCartException;

    void addOrderLineItemToCart(Long groupCartId, OrderLineItemEntity orderLineItemEntity) throws GroupCartNotFoundException;

    void checkOutCart(Long groupCartId) throws GroupCartNotFoundException;

}
