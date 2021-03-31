/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditCardEntity;
import javax.ejb.Local;
import util.exception.CreateNewCreditCardException;
import util.exception.CreditCardNotFoundException;
import util.exception.InputDataValidationException;

@Local
public interface CreditCardSessionBeanLocal {
    
    CreditCardEntity createNewCreditCard(CreditCardEntity newCreditCardEntity) throws InputDataValidationException, CreateNewCreditCardException;
    
    CreditCardEntity retrieveCreditCartById(Long creditCardId) throws CreditCardNotFoundException ;
    
    CreditCardEntity retrieveCreditCardByNumber(Long ccNumber) throws CreditCardNotFoundException;
    
}
