/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AccountEntity;
import javax.ejb.Local;
import util.exception.AccountNotFoundException;
import util.exception.AccountUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author winyfebriny
 */
@Local
public interface AccountSessionBeanLocal {
    Long createNewAccount(AccountEntity newAccountEntity) throws AccountUsernameExistException, UnknownPersistenceException, InputDataValidationException;
    
    AccountEntity retrieveAccountByAccountId(Long accountId) throws AccountNotFoundException;
    
    AccountEntity retrieveAccountByUsername(String username) throws AccountNotFoundException;
    
    AccountEntity accountLogin(String username, String password) throws InvalidLoginCredentialException;
    
    
}
