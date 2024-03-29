/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.StaffEntity;
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
import util.exception.DeleteStaffException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateStaffException;
import util.security.CryptographicHelper;

@Stateless
public class StaffEntitySessionBean implements StaffEntitySessionBeanLocal {

    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;

    public StaffEntitySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Long createNewStaff(StaffEntity newStaffEntity) throws StaffUsernameExistException, UnknownPersistenceException, InputDataValidationException {
        Set<ConstraintViolation<StaffEntity>> constraintViolations = validator.validate(newStaffEntity);

        if (constraintViolations.isEmpty()) {
            try {
                entityManager.persist(newStaffEntity);
                entityManager.flush();

                return newStaffEntity.getStaffId();
                
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new StaffUsernameExistException();
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
    public List<StaffEntity> retrieveAllStaffs() {
        Query query = entityManager.createQuery("SELECT s FROM StaffEntity s");

        return query.getResultList();
    }

    @Override
    public StaffEntity retrieveStaffByStaffId(Long staffId) throws StaffNotFoundException {
        StaffEntity staffEntity = entityManager.find(StaffEntity.class, staffId);

        if (staffEntity != null) {
            return staffEntity;
        } else {
            throw new StaffNotFoundException("Staff ID " + staffId + " does not exist!");
        }
    }

    @Override
    public StaffEntity retrieveStaffByUsername(String username) throws StaffNotFoundException {
        Query query = entityManager.createQuery("SELECT s FROM StaffEntity s WHERE s.username=:inUsername");
        query.setParameter("inUsername", username);

        try {
            return (StaffEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new StaffNotFoundException("Staff Username " + username + " does not exist!");
        }
    }

    @Override
    public StaffEntity staffLogin(String username, String password) throws InvalidLoginCredentialException {
        try {
            StaffEntity staffEntity = retrieveStaffByUsername(username);
            System.out.println("Staff retrieved!!!" + staffEntity.getUsername());
            String passwordHash = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + staffEntity.getSalt()));

            System.out.println(passwordHash);
            System.out.println(staffEntity.getPassword() + "   &&&&&&&&&");
            if (staffEntity.getPassword().equals(passwordHash)) {
//                staffEntity.getSaleTransactionEntities().size();  
                System.out.println("Password correct!!!");
                return staffEntity;
            } else {
                throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
            }
        } catch (StaffNotFoundException ex) {
            throw new InvalidLoginCredentialException("Username does not exist or invalid password!");
        }
    }

    @Override
    public void updateStaff(StaffEntity staffEntity) throws StaffNotFoundException, UpdateStaffException, InputDataValidationException {
        if (staffEntity != null && staffEntity.getStaffId() != null) {
            Set<ConstraintViolation<StaffEntity>> constraintViolations = validator.validate(staffEntity);

            if (constraintViolations.isEmpty()) {
                StaffEntity staffEntityToUpdate = retrieveStaffByStaffId(staffEntity.getStaffId());

                if (staffEntityToUpdate.getUsername().equals(staffEntity.getUsername())) {
                    staffEntityToUpdate.setFirstName(staffEntity.getFirstName());
                    staffEntityToUpdate.setLastName(staffEntity.getLastName());
                    staffEntityToUpdate.setAccessRightEnum(staffEntity.getAccessRightEnum());
                } else {
                    throw new UpdateStaffException("Username of staff record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new StaffNotFoundException("Staff ID not provided for staff to be updated");
        }
    }

    // Updated in v4.1
    @Override
    public void deleteStaff(Long staffId) throws StaffNotFoundException, DeleteStaffException {
        StaffEntity staffEntityToRemove = retrieveStaffByStaffId(staffId);

//        if(staffEntityToRemove.getSaleTransactionEntities().isEmpty())
//        {
        entityManager.remove(staffEntityToRemove);
//        }
//        else
//        {
//            // New in v4.1 to prevent deleting staff with existing sale transaction(s)
//            throw new DeleteStaffException("Staff ID " + staffId + " is associated with existing sale transaction(s) and cannot be deleted!");
//        }
    }

    // Added in v4.2
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<StaffEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }
}
