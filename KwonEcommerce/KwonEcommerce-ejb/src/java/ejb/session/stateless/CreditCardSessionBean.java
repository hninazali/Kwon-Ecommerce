/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.CreditCardEntity;
import entity.CustomerEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
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
import util.exception.CreateNewCreditCardException;
import util.exception.CreditCardNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;

@Stateless
public class CreditCardSessionBean implements CreditCardSessionBeanLocal 
{

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public CreditCardSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public CreditCardEntity createNewCreditCard(CreditCardEntity newCreditCardEntity) throws InputDataValidationException, CreateNewCreditCardException {
        Set<ConstraintViolation<CreditCardEntity>> constraintViolations = validator.validate(newCreditCardEntity);

        if (constraintViolations.isEmpty()) {
            try {

//                CustomerEntity ce = newCreditCardEntity.getCustomerEntity();
//                ce.getCreditCardEntities().add(newCreditCardEntity);
                entityManager.persist(newCreditCardEntity);
                entityManager.flush();
                return newCreditCardEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null
                        && ex.getCause().getCause() != null
                        && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new CreateNewCreditCardException("Credit card with same number already exist");
                } else {
                    throw new CreateNewCreditCardException("An unexpected error has occurred: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw new CreateNewCreditCardException("An unexpected error has occurred: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public CreditCardEntity createCreditCard(Long customerId, CreditCardEntity newCreditCardEntity) throws InputDataValidationException, CreateNewCreditCardException {
        Set<ConstraintViolation<CreditCardEntity>> constraintViolations = validator.validate(newCreditCardEntity);

        if (constraintViolations.isEmpty()) {
            try {

                CustomerEntity ce = customerSessionBeanLocal.retrieveCustomerById(customerId);
                ce.getCreditCardEntities().add(newCreditCardEntity);
                entityManager.persist(newCreditCardEntity);
                entityManager.flush();
                return newCreditCardEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null
                        && ex.getCause().getCause() != null
                        && ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException")) {
                    throw new CreateNewCreditCardException("Credit card with same number already exist");
                } else {
                    throw new CreateNewCreditCardException("An unexpected error has occurred: " + ex.getMessage());
                }
            } catch (Exception ex) {
                throw new CreateNewCreditCardException("An unexpected error has occurred: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<CreditCardEntity> retrieveCustomerCreditCard(Long customerId) throws CustomerNotFoundException
    {
        CustomerEntity customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
        customer.getCreditCardEntities().size();
        
        return customer.getCreditCardEntities();
    }

    @Override
    public CreditCardEntity retrieveCreditCartById(Long creditCardId) throws CreditCardNotFoundException {
        CreditCardEntity creditCardEntity = entityManager.find(CreditCardEntity.class, creditCardId);

        if (creditCardEntity != null) {
            return creditCardEntity;
        } else {
            throw new CreditCardNotFoundException("Staff ID " + creditCardId + " does not exist!");
        }
    }

    @Override
    public CreditCardEntity retrieveCreditCardByNumber(Long ccNumber) throws CreditCardNotFoundException {
        Query query = entityManager.createQuery("SELECT cc FROM CreditCardEntity cc WHERE cc.number=:inNumber");
        query.setParameter("inNumber", ccNumber);

        try {
            return (CreditCardEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new CreditCardNotFoundException("Credit Card " + ccNumber + " does not exist!");
        }
    }
    
    @Override
    public void deleteCreditCard(Long customerId, Long creditCardId) throws CustomerNotFoundException, CreditCardNotFoundException
    {
        CustomerEntity customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
        CreditCardEntity creditCard = retrieveCreditCartById(creditCardId);
        
        customer.getCreditCardEntities().remove(creditCard);
        
        entityManager.remove(creditCard);
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<CreditCardEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
