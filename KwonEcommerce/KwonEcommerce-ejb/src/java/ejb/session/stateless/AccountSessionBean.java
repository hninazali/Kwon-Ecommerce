/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AccountEntity;
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
import util.exception.AccountNotFoundException;
import util.exception.AccountUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.security.CryptographicHelper;

/**
 *
 * @author winyfebriny
 */
@Stateless
public class AccountSessionBean implements AccountSessionBeanLocal {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;

    public AccountSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewAccount(AccountEntity newAccountEntity) throws AccountUsernameExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<AccountEntity>> constraintViolations = validator.validate(newAccountEntity);

        if (constraintViolations.isEmpty()) {
            try {
                entityManager.persist(newAccountEntity);
                entityManager.flush();

                return newAccountEntity.getAccountId();
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new AccountUsernameExistException();
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
    public AccountEntity retrieveAccountByAccountId(Long accountId) throws AccountNotFoundException {
        AccountEntity accountEntity = entityManager.find(AccountEntity.class, accountId);

        if (accountEntity != null) {
            return accountEntity;
        } else {
            throw new AccountNotFoundException("Account ID " + accountId + " does not exist!");
        }
    }

    @Override
    public AccountEntity retrieveAccountByUsername(String username) throws AccountNotFoundException {
        Query query = entityManager.createQuery("SELECT a FROM AccountEntity a WHERE a.username=:inUsername");
        query.setParameter("inUsername", username);

        try {
            return (AccountEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new AccountNotFoundException("Account with username " + username + " does not exist!");
        }
    }

    @Override
    public AccountEntity accountLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            AccountEntity accountEntity = retrieveAccountByUsername(username);
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + accountEntity.getSalt()));

            if (accountEntity.getPassword().equals(passwordHash)) {
                return accountEntity;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (AccountNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AccountEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
