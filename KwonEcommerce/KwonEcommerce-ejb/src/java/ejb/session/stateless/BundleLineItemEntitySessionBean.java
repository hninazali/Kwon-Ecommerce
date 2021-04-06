/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BundleEntity;
import entity.BundleLineItemEntity;
import entity.ProductEntity;
import entity.TagEntity;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.BundleNotFoundException;
import util.exception.DeleteBundleException;
import util.exception.InputDataValidationException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.TagNotFoundException;
import util.exception.UpdateBundleException;

/**
 *
 * @author User
 */
@Stateless
public class BundleLineItemEntitySessionBean implements BundleLineItemEntitySessionBeanLocal {
    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
     @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;
//    @EJB
//    private SaleTransactionEntitySessionBeanLocal saleTransactionEntitySessionBeanLocal;
    @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    
    public BundleLineItemEntitySessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
  
   
     // Added in v4.2
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<BundleEntity>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
    
    // Newly addded in v5.1
    
    
    @Override
    public BundleLineItemEntity retrieveBundleLineItemByBundleLineItemId(Long bundleLineItemId) throws BundleNotFoundException
    {
        BundleLineItemEntity bundleLineItemEntity = entityManager.find(BundleLineItemEntity.class, bundleLineItemId);
        
        if(bundleLineItemEntity != null)
        {        
            return bundleLineItemEntity;
        }
        else
        {
            throw new BundleNotFoundException("Product ID " + bundleLineItemId + " does not exist!");
        }               
    }
    
    
    // Updated in v4.1
    // Updated in v4.2 to include reorderQuantity and bean validation
    // Updated in v5.0 to include association with new category entity
    // Updated in v5.1 with category entity and tag entity processing
    
    @Override
    public void updateBundle(BundleEntity bundleEntity, List<Long> bundleLineItemIds, List<Long> tagIds) throws BundleNotFoundException, TagNotFoundException, UpdateBundleException, InputDataValidationException, ProductNotFoundException
    {
        if(bundleEntity != null && bundleEntity.getBundleId()!= null)
        {
            Set<ConstraintViolation<BundleEntity>>constraintViolations = validator.validate(bundleEntity);
        
            if(constraintViolations.isEmpty())
            {
                BundleEntity bundleEntityToUpdate = retrieveBundleByBundleId(bundleEntity.getBundleId());

                if(bundleEntityToUpdate.getSkuCode().equals(bundleEntity.getSkuCode()))
                {
//                    // Added in v5.1
//                    if(categoryId != null && (!productEntityToUpdate.getCategoryEntity().getCategoryId().equals(categoryId)))
//                    {
//                        CategoryEntity categoryEntityToUpdate = categoryEntitySessionBeanLocal.retrieveCategoryByCategoryId(categoryId);
//                        
//                        if(!categoryEntityToUpdate.getSubCategoryEntities().isEmpty())
//                        {
//                            throw new UpdateProductException("Selected category for the new product is not a leaf category");
//                        }
//                        
//                        productEntityToUpdate.setCategoryEntity(categoryEntityToUpdate);
//                    }
                    
                    // Added in v5.1
                    if(tagIds != null)
                    {
                        for(TagEntity tagEntity:bundleEntityToUpdate.getTagEntities())
                        {
                            tagEntity.getBundleEntities().remove(bundleEntityToUpdate);
                        }
                        
                        bundleEntityToUpdate.getTagEntities().clear();
                        
                        for(Long tagId:tagIds)
                        {
                            TagEntity tagEntity = tagEntitySessionBeanLocal.retrieveTagByTagId(tagId);
                            bundleEntityToUpdate.addTag(tagEntity);
                        }
                    }
                    
                     if(bundleLineItemIds != null)
                    {
//                        for(ProductEntity existingProduct:bundleEntityToUpdate.getProductEntities())
//                        {
//                            existingProduct.getBundleEntities().remove(bundleEntityToUpdate);
//                        }
                        bundleEntityToUpdate.getCategoryEntities().clear();
                        bundleEntityToUpdate.getBundleLineItems().clear();
                        
                        for(Long newBundleLineItemId:bundleLineItemIds)
                        {
                            BundleLineItemEntity updatedBundleLineItem = productEntitySessionBeanLocal.retrieveProductByProductId(newProductId);
                            bundleEntityToUpdate.addProduct(updatedProduct);
                        }
                    }
                                
                    bundleEntityToUpdate.setName(bundleEntity.getName());
                    bundleEntityToUpdate.setDescription(bundleEntity.getDescription());
                    bundleEntityToUpdate.setUnitPrice(bundleEntity.getUnitPrice());
                    // Removed in v5.0
                    //productEntityToUpdate.setCategory(bundleEntity.getCategory());
                    // Added in v5.1
                    bundleEntityToUpdate.setBundleRating((bundleEntity.getBundleRating()));
                }
                else
                {
                    throw new UpdateBundleException("SKU Code of bundle record to be updated does not match the existing record");
                }
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new BundleNotFoundException("Bundle ID not provided for bundle to be updated");
        }
    }
    
    @Override
    public void debitQuantityOnHand(Long bundleId, Integer quantityToDebit) throws BundleNotFoundException, ProductInsufficientQuantityOnHandException
    {
        BundleEntity bundleEntity = retrieveBundleByBundleId(bundleId);
        
        if(bundleEntity.getQuantityOnHand() >= quantityToDebit)
        {
            bundleEntity.setQuantityOnHand(bundleEntity.getQuantityOnHand() - quantityToDebit);
            for (BundleLineItemEntity bundleLineItem: bundleEntity.getBundleLineItems()) {
                bundleLineItem.getProductEntity().debitQuantityOnHand();
            }
        }
        else
        {
            throw new ProductInsufficientQuantityOnHandException("Product " + bundleEntity.getSkuCode() + " quantity on hand is " + bundleEntity.getQuantityOnHand() + " versus quantity to debit of " + quantityToDebit);
        }
    }
    
    
    
    // Added in v4.1
    
    @Override
    public void creditQuantityOnHand(Long productId, Integer quantityToCredit) throws ProductNotFoundException
    {
        ProductEntity productEntity = retrieveProductByProductId(productId);
        productEntity.setQuantityOnHand(productEntity.getQuantityOnHand() + quantityToCredit);
    }
}


