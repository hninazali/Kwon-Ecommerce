/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import java.util.List;
import javax.ejb.Local;
import util.exception.BanCustomerException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.DeleteCustomerException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateCustomerException;

@Local
public interface CustomerSessionBeanLocal {

    Long createNewCustomer(CustomerEntity newCustomerEntity) throws CustomerUsernameExistException, UnknownPersistenceException, InputDataValidationException;

    List<CustomerEntity> retrieveAllCustomers();

    CustomerEntity retrieveCustomerById(Long customerId) throws CustomerNotFoundException;

    CustomerEntity retrieveCustomerByUsername(String username) throws CustomerNotFoundException;

    CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException;

    void updateCustomer(CustomerEntity customerEntity) throws CustomerNotFoundException, UpdateCustomerException, InputDataValidationException;

    void deleteCustomer(Long customerId) throws CustomerNotFoundException, DeleteCustomerException;
    
    void banCustomer(Long customerId) throws BanCustomerException, CustomerNotFoundException;
    
}
