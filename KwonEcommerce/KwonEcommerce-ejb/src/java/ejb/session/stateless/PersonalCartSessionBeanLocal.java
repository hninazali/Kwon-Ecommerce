/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OrderLineItemEntity;
import entity.PersonalCartEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewPersonalCartException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.PersonalCartNotFoundException;

@Local
public interface PersonalCartSessionBeanLocal {

    PersonalCartEntity createPersonalCartEntity(PersonalCartEntity newPersonalCartEntity) throws InputDataValidationException, CreateNewPersonalCartException;

    List<PersonalCartEntity> retrieveAllPersonalCartEntities();

    PersonalCartEntity retrievePersonalCartById(Long personalCartId) throws PersonalCartNotFoundException;

    void addOrderLineItemToCart(Long personalCartId, OrderLineItemEntity orderLineItemEntity) throws PersonalCartNotFoundException;

    void checkOutCart(Long customerId, Long personalCartId) throws PersonalCartNotFoundException, CustomerNotFoundException;
}
