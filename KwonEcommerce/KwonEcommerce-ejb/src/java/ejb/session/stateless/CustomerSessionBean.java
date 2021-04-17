/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CustomerEntity;
import entity.PersonalCartEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.BanCustomerException;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.DeleteCustomerException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateCustomerException;
import util.security.CryptographicHelper;

@Stateless
public class CustomerSessionBean implements CustomerSessionBeanLocal {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;

    public CustomerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewCustomer(CustomerEntity newCustomerEntity) throws CustomerUsernameExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(newCustomerEntity);

        if (constraintViolations.isEmpty()) {
            try {
                PersonalCartEntity pc = new PersonalCartEntity();
                newCustomerEntity.setPersonalCartEntity(pc);
                entityManager.persist(newCustomerEntity);
                entityManager.flush();
                System.out.println("Customer created");
                return newCustomerEntity.getCustomerId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new CustomerUsernameExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    @Override
    public List<CustomerEntity> retrieveAllCustomers() {
        Query query = entityManager.createQuery("SELECT c FROM CustomerEntity c");
        List<CustomerEntity> customerEntities = query.getResultList();
        
        for(CustomerEntity c : customerEntities)
        {
            c.getCreditCardEntities().size();
            c.getGroupCartEntities().size();
            c.getOrderLineItemEntities().size();
            c.getOrderTransactionEntities().size();
        }
        
        return customerEntities;
    }

    @Override
    public CustomerEntity retrieveCustomerById(Long customerId) throws CustomerNotFoundException {
        CustomerEntity customerEntity = entityManager.find(CustomerEntity.class, customerId);
        
        customerEntity.getGroupCartEntities().size();

        if (customerEntity != null) {
            return customerEntity;
        } else {
            throw new CustomerNotFoundException("Customer ID " + customerId + " does not exist!");
        }
    }

    @Override
    public CustomerEntity retrieveCustomerByUsername(String username) throws CustomerNotFoundException {
        Query query = entityManager.createQuery("SELECT c FROM CustomerEntity c WHERE c.username = :inUsername");
        query.setParameter("inUsername", username);
        CustomerEntity customerEntity = (CustomerEntity) query.getSingleResult();

        if (customerEntity != null) {
            return customerEntity;
        } else {
            throw new CustomerNotFoundException("Customer Username " + customerEntity.getUsername() + " does not exist!");
        }
    }

    @Override
    public CustomerEntity customerLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            CustomerEntity customerEntity = retrieveCustomerByUsername(username);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + customerEntity.getSalt()));

            if (customerEntity.getPassword().equals(passwordHash)) {
                customerEntity.getCreditCardEntities();
                customerEntity.getGroupCartEntities();
                customerEntity.getOrderLineItemEntities();
                customerEntity.getOrderTransactionEntities();
                return customerEntity;
            } else {
                throw new InvalidLoginCredentialException("Wrong Credentials!!!");
            }
        } catch (CustomerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    @Override
    public void updateCustomer(CustomerEntity customerEntity) throws CustomerNotFoundException, UpdateCustomerException, InputDataValidationException {
        if (customerEntity != null && customerEntity.getCustomerId() != null) {
            Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(customerEntity);

            if (constraintViolations.isEmpty()) {
                CustomerEntity customerEntityToUpdate = retrieveCustomerById(customerEntity.getCustomerId());

                if (customerEntityToUpdate.getUsername().equals(customerEntity.getUsername())) {
                    customerEntityToUpdate.setFirstName(customerEntity.getFirstName());
                    customerEntityToUpdate.setLastName(customerEntity.getLastName());
                    customerEntityToUpdate.setAddress(customerEntity.getAddress());
                    customerEntityToUpdate.setPostalCode(customerEntity.getPostalCode());
                    //System.out.println(customerEntityToUpdate.getPassword());
                } else {
                    throw new UpdateCustomerException("Email of customer record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new CustomerNotFoundException("Staff ID not provided for staff to be updated");
        }
    }
    
    @Override
    public void updatePassword(CustomerEntity customerEntity, String password, String oldPassword) throws CustomerNotFoundException, UpdateCustomerException, InputDataValidationException, InvalidLoginCredentialException {
        if (customerEntity != null && customerEntity.getCustomerId() != null) {
            Set<ConstraintViolation<CustomerEntity>> constraintViolations = validator.validate(customerEntity);
            
            CustomerEntity customerTemp = this.retrieveCustomerById(customerEntity.getCustomerId());
            System.out.println(password + "   *******");
            
            if (customerTemp != null)
            {
                if (constraintViolations.isEmpty()) {
                    CustomerEntity customerEntityToUpdate = retrieveCustomerById(customerEntity.getCustomerId());

                    if (customerEntityToUpdate.getUsername().equals(customerEntity.getUsername())) {
                        customerEntityToUpdate.setPassword(password);
                    } else {
                        throw new UpdateCustomerException("Email of customer record to be updated does not match the existing record");
                    }
                } else {
                    throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
                }
            }
            else
            {
                throw new InvalidLoginCredentialException("Old Password is incorrect!!!");
            }
        } else {
            throw new CustomerNotFoundException("Staff ID not provided for staff to be updated");
        }
    }

    @Override
    public void deleteCustomer(Long customerId) throws CustomerNotFoundException, DeleteCustomerException {
        CustomerEntity customerEntityToRemove = retrieveCustomerById(customerId);
        entityManager.remove(customerEntityToRemove);
    }
    
    @Override
    public void banCustomer(Long customerId) throws BanCustomerException, CustomerNotFoundException
    {
        CustomerEntity customerEntity = retrieveCustomerById(customerId);
        if(customerEntity.getBanned()) {
            throw new BanCustomerException("This customer has been banned before!");
        }
        else 
        {
            customerEntity.setBanned(Boolean.TRUE);
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CustomerEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
