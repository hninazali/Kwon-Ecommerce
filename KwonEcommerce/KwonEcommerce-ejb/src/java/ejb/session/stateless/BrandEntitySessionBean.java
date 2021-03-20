package ejb.session.stateless;

import entity.BrandEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.CreateNewBrandException;
import util.exception.InputDataValidationException;
import util.exception.BrandNotFoundException;
import util.exception.DeleteBrandException;
import util.exception.UpdateBrandException;

@Stateless
public class BrandEntitySessionBean implements BrandEntitySessionBeanLocal {

    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;

    private final ValidatorFactory validatorFactory;
    private final Validator validator;

    public BrandEntitySessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public BrandEntity createNewBrandEntity(BrandEntity newBrandEntity) throws InputDataValidationException, CreateNewBrandException
    {
        Set<ConstraintViolation<BrandEntity>>constraintViolations = validator.validate(newBrandEntity);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                entityManager.persist(newBrandEntity);
                entityManager.flush();

                return newBrandEntity;
            }
            catch(PersistenceException ex)
            {                
                if(ex.getCause() != null && 
                        ex.getCause().getCause() != null &&
                        ex.getCause().getCause().getClass().getSimpleName().equals("SQLIntegrityConstraintViolationException"))
                {
                    throw new CreateNewBrandException("Brand with same name already exist");
                }
                else
                {
                    throw new CreateNewBrandException("An unexpected error has occurred: " + ex.getMessage());
                }
            }
            catch(Exception ex)
            {                
                throw new CreateNewBrandException("An unexpected error has occurred: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public List<BrandEntity> retrieveAllBrands()
    {
        Query query = entityManager.createQuery("SELECT b FROM BrandEntity b ORDER BY b.name ASC");
        List<BrandEntity> brandEntities = query.getResultList();
        
        for(BrandEntity brandEntity:brandEntities)
        {            
            brandEntity.getProductEntities().size();
        }
        
        return brandEntities;
    }
    
    @Override
    public BrandEntity retrieveBrandByBrandId(Long brandId) throws BrandNotFoundException
    {
        BrandEntity brandEntity = entityManager.find(BrandEntity.class, brandId);
        
        if(brandEntity != null)
        {
            return brandEntity;
        }
        else
        {
            throw new BrandNotFoundException("Brand ID " + brandId + " does not exist!");
        }               
    }
    
    @Override
    public void updateBrand(BrandEntity brandEntity) throws InputDataValidationException, BrandNotFoundException, UpdateBrandException
    {
        Set<ConstraintViolation<BrandEntity>>constraintViolations = validator.validate(brandEntity);
        
        if(constraintViolations.isEmpty())
        {
            if(brandEntity.getBrandId()!= null)
            {
                BrandEntity brandEntityToUpdate = retrieveBrandByBrandId(brandEntity.getBrandId());
                
                Query query = entityManager.createQuery("SELECT b FROM BrandEntity b WHERE b.name = :inName AND b.brandId <> :inBrandId");
                query.setParameter("inName", brandEntity.getName());
                query.setParameter("inBrandId", brandEntity.getBrandId());
                
                if(!query.getResultList().isEmpty())
                {
                    throw new UpdateBrandException("The name of the brand to be updated is duplicated");
                }
                
                brandEntityToUpdate.setName(brandEntity.getName());                               
            }
            else
            {
                throw new BrandNotFoundException("Brand ID not provided for brand to be updated");
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    @Override
    public void deleteBrand(Long brandId) throws BrandNotFoundException, DeleteBrandException
    {
        BrandEntity brandEntityToRemove = retrieveBrandByBrandId(brandId);
        
        if(!brandEntityToRemove.getProductEntities().isEmpty())
        {
            throw new DeleteBrandException("Brand ID " + brandId + " is associated with existing products and cannot be deleted!");
        }
        else
        {
            entityManager.remove(brandEntityToRemove);
        }                
    } 
     
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<BrandEntity>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
}
