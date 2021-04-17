/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditCardEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.CreateNewCreditCardException;
import util.exception.CreditCardNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;

@Local
public interface CreditCardSessionBeanLocal {
    
    CreditCardEntity createNewCreditCard(CreditCardEntity newCreditCardEntity) throws InputDataValidationException, CreateNewCreditCardException;
    
    CreditCardEntity retrieveCreditCartById(Long creditCardId) throws CreditCardNotFoundException ;
    
    CreditCardEntity retrieveCreditCardByNumber(Long ccNumber) throws CreditCardNotFoundException;

    public CreditCardEntity createCreditCard(Long customerId, CreditCardEntity newCreditCardEntity) throws InputDataValidationException, CreateNewCreditCardException;

    public List<CreditCardEntity> retrieveCustomerCreditCard(Long customerId) throws CustomerNotFoundException;
    
}
