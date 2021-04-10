/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BrandEntity;
import entity.BundleEntity;
import entity.BundleLineItemEntity;
import entity.CategoryEntity;
import entity.ProductEntity;
import entity.TagEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import util.exception.BundleInsufficientQuantityOnHandException;
import util.exception.BundleNotFoundException;
import util.exception.BundleSkuCodeExistException;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewBrandException;
import util.exception.CreateNewBundleException;
import util.exception.CreateNewProductException;
import util.exception.DeleteBundleException;
import util.exception.DeleteProductException;
import util.exception.InputDataValidationException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateBundleException;
import util.exception.UpdateProductException;

/**
 *
 * @author hninazali
 */
@Stateless
public class BundleEntitySessionBean implements BundleEntitySessionBeanLocal {

    @EJB(name = "CategoryEntitySessionBeanLocal")
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
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

    @EJB
    private BundleLineItemEntitySessionBeanLocal bundleLineItemEntitySessionBeanLocal;

    public BundleEntitySessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public BundleEntity createNewBundle(BundleEntity newBundleEntity, List<Long> tagIds) throws BundleSkuCodeExistException, UnknownPersistenceException, InputDataValidationException, CreateNewBundleException {
        Set<ConstraintViolation<BundleEntity>> constraintViolations = validator.validate(newBundleEntity);

        if (constraintViolations.isEmpty()) {
            try {

                if (tagIds != null && (!tagIds.isEmpty())) {
                    for (Long tagId : tagIds) {
                        TagEntity tagEntity = tagEntitySessionBeanLocal.retrieveTagByTagId(tagId);
                        newBundleEntity.addTag(tagEntity);
                    }
                }

                entityManager.flush();

                return newBundleEntity;
            } catch (PersistenceException ex) {
                if (ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    if (ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new BundleSkuCodeExistException();
                    } else {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                } else {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            } catch (TagNotFoundException ex) {
                throw new CreateNewBundleException("An error has occurred while creating the new bundle: " + ex.getMessage());
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }

    // Added in v4.2
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<BundleEntity>> constraintViolations) {
        String msg = "Input data validation error!:";

        for (ConstraintViolation constraintViolation : constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }

        return msg;
    }

    @Override
    public List<BundleEntity> retrieveAllBundles() {
        Query query = entityManager.createQuery("SELECT b FROM BundleEntity b ORDER BY b.skuCode ASC");
        List<BundleEntity> bundleEntities = query.getResultList();

        for (BundleEntity bundleEntity : bundleEntities) {
            bundleEntity.getCategoryEntities();
            bundleEntity.getTagEntities().size();
        }

        return bundleEntities;
    }

    // Newly addded in v5.1
    @Override
    public List<BundleEntity> searchBundlesByName(String searchString) {
        Query query = entityManager.createQuery("SELECT b FROM BundleEntity b WHERE b.name LIKE :inSearchString ORDER BY b.skuCode ASC");
        query.setParameter("inSearchString", "%" + searchString + "%");
        List<BundleEntity> bundleEntities = query.getResultList();

        for (BundleEntity productEntity : bundleEntities) {
            productEntity.getCategoryEntities();
            productEntity.getTagEntities().size();
        }

        return bundleEntities;
    }

    @Override
    public List<BundleEntity> filterBundlesByTags(List<Long> tagIds, String condition) {
        List<BundleEntity> bundleEntities = new ArrayList<>();

        if (tagIds == null || tagIds.isEmpty() || (!condition.equals("AND") && !condition.equals("OR"))) {
            return bundleEntities;
        } else {
            if (condition.equals("OR")) {
                Query query = entityManager.createQuery("SELECT DISTINCT pe FROM BundleEntity pe, IN (pe.tagEntities) te WHERE te.tagId IN :inTagIds ORDER BY pe.skuCode ASC");
                query.setParameter("inTagIds", tagIds);
                bundleEntities = query.getResultList();
            } else // AND
            {
                String selectClause = "SELECT pe FROM BundleEntity pe";
                String whereClause = "";
                Boolean firstTag = true;
                Integer tagCount = 1;

                for (Long tagId : tagIds) {
                    selectClause += ", IN (pe.tagEntities) te" + tagCount;

                    if (firstTag) {
                        whereClause = "WHERE te1.tagId = " + tagId;
                        firstTag = false;
                    } else {
                        whereClause += " AND te" + tagCount + ".tagId = " + tagId;
                    }

                    tagCount++;
                }

                String jpql = selectClause + " " + whereClause + " ORDER BY pe.skuCode ASC";
                Query query = entityManager.createQuery(jpql);
                bundleEntities = query.getResultList();
            }

            for (BundleEntity bundleEntity : bundleEntities) {
                bundleEntity.getCategoryEntities();
                bundleEntity.getTagEntities().size();
            }

            Collections.sort(bundleEntities, new Comparator<BundleEntity>() {
                public int compare(BundleEntity pe1, BundleEntity pe2) {
                    return pe1.getSkuCode().compareTo(pe2.getSkuCode());
                }
            });

            return bundleEntities;
        }
    }

    @Override
    public List<BundleEntity> filterBundlesByPrice(BigDecimal startPrice, BigDecimal endPrice) {
        List<BundleEntity> bundleEntities = new ArrayList<>();

        Query query = entityManager.createQuery("SELECT DISTINCT pe FROM BundleEntity pe WHERE pe.unitPrice BETWEEN :inStartPrice AND :inEndPrice ORDER BY pe.skuCode ASC");
        query.setParameter("inStartPrice", startPrice);
        query.setParameter("inEndPrice", endPrice);
        bundleEntities = query.getResultList();

        return bundleEntities;
    }

    @Override
    public BundleEntity retrieveBundleByBundleId(Long bundleId) throws BundleNotFoundException {
        BundleEntity bundleEntity = entityManager.find(BundleEntity.class, bundleId);

        if (bundleEntity != null) {
            bundleEntity.getCategoryEntities();
            bundleEntity.getTagEntities().size();

            return bundleEntity;
        } else {
            throw new BundleNotFoundException("Product ID " + bundleId + " does not exist!");
        }
    }

    @Override
    public BundleEntity retrieveBundleByBundleSkuCode(String skuCode) throws BundleNotFoundException {
        Query query = entityManager.createQuery("SELECT p FROM BundleEntity p WHERE p.skuCode = :inSkuCode");
        query.setParameter("inSkuCode", skuCode);

        try {
            BundleEntity bundleEntity = (BundleEntity) query.getSingleResult();
            bundleEntity.getCategoryEntities();
            bundleEntity.getTagEntities().size();

            return bundleEntity;
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new BundleNotFoundException("Sku Code " + skuCode + " does not exist!");
        }
    }

    // Updated in v4.1
    // Updated in v4.2 to include reorderQuantity and bean validation
    // Updated in v5.0 to include association with new category entity
    // Updated in v5.1 with category entity and tag entity processing
    @Override
    public void updateBundle(BundleEntity bundleEntity) throws BundleNotFoundException, TagNotFoundException, UpdateBundleException, InputDataValidationException, ProductNotFoundException, CategoryNotFoundException {
        Integer currQty = 0;
        BigDecimal price = new BigDecimal(0);
        if (bundleEntity != null && bundleEntity.getBundleId() != null) {
            Set<ConstraintViolation<BundleEntity>> constraintViolations = validator.validate(bundleEntity);

            if (constraintViolations.isEmpty()) {
                BundleEntity bundleEntityToUpdate = retrieveBundleByBundleId(bundleEntity.getBundleId());

                if (bundleEntityToUpdate.getSkuCode().equals(bundleEntity.getSkuCode())) {
                    if (bundleEntityToUpdate.getBundleLineItems().size() == bundleEntity.getBundleLineItems().size())
                    {
                        for (BundleLineItemEntity lineItem2 : bundleEntity.getBundleLineItems())
                        {
                            for (BundleLineItemEntity lineItem : bundleEntityToUpdate.getBundleLineItems())
                            {
                                if (lineItem.getProductEntity().getName().equals(lineItem2.getProductEntity().getName()))
                                    lineItem.setQuantity(lineItem2.getQuantity());
                            }
                        }
                    }
                    else
                    {
                        for (BundleLineItemEntity temp : bundleEntityToUpdate.getBundleLineItems())
                        {
                            bundleEntityToUpdate.getBundleLineItems().remove(temp);
                            entityManager.remove(temp);
                        }
                        for (BundleLineItemEntity bundleLineItem : bundleEntity.getBundleLineItems())
                        {
                            ProductEntity tempProd = bundleLineItem.getProductEntity();
                            BundleLineItemEntity lineItemTemp = new BundleLineItemEntity(bundleLineItem.getSerialNumber(), tempProd, 
                                    bundleLineItem.getQuantity(), tempProd.getUnitPrice().multiply(new BigDecimal(bundleLineItem.getQuantity())));
                            entityManager.persist(lineItemTemp);
                            bundleEntityToUpdate.getBundleLineItems().add(lineItemTemp);
                        }
                    }
                    
                    for (CategoryEntity cat : bundleEntityToUpdate.getCategoryEntities())
                    {
                        CategoryEntity categoryEntityToUpdate = categoryEntitySessionBeanLocal.retrieveCategoryByCategoryId(cat.getCategoryId());
                        categoryEntityToUpdate.getBundleEntities().remove(bundleEntityToUpdate);
                        bundleEntityToUpdate.getCategoryEntities().remove(cat);
                    }
                    for (TagEntity tagTemp : bundleEntityToUpdate.getTagEntities())
                    {
                        TagEntity tagEntityToUpdate = tagEntitySessionBeanLocal.retrieveTagByTagId(tagTemp.getTagId());
                        tagEntityToUpdate.getBundleEntities().remove(bundleEntityToUpdate);
                        bundleEntityToUpdate.getTagEntities().remove(tagTemp);
                    }
                    for (BundleLineItemEntity lineItems : bundleEntityToUpdate.getBundleLineItems())
                    {
                        ProductEntity prod = lineItems.getProductEntity();
                        
                        CategoryEntity category = prod.getCategoryEntity();
                        category.getBundleEntities().add(bundleEntityToUpdate);
                        bundleEntityToUpdate.getCategoryEntities().add(category);
                        
                        List<TagEntity> tags = prod.getTagEntities();
                        for (TagEntity eachTag : tags)
                        {
                            eachTag.getBundleEntities().add(bundleEntityToUpdate);
                            bundleEntityToUpdate.getTagEntities().add(eachTag);
                        }
                        price.add(lineItems.getSubTotal());
                    }
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

//                    // Added in v5.1
//                    if (tagIds != null) {
//                        for (TagEntity tagEntity : bundleEntityToUpdate.getTagEntities()) {
//                            tagEntity.getBundleEntities().remove(bundleEntityToUpdate);
//                        }
//
//                        bundleEntityToUpdate.getTagEntities().clear();
//
//                        for (Long tagId : tagIds) {
//                            TagEntity tagEntity = tagEntitySessionBeanLocal.retrieveTagByTagId(tagId);
//                            bundleEntityToUpdate.addTag(tagEntity);
//                        }
//                    }

//                    if (bundleLineItemIds != null) {
////                        for(ProductEntity existingProduct:bundleEntityToUpdate.getProductEntities())
////                        {
////                            existingProduct.getBundleEntities().remove(bundleEntityToUpdate);
////                        }
//                        bundleEntityToUpdate.getCategoryEntities().clear();
//                        bundleEntityToUpdate.getBundleLineItems().clear();
//
//                        for (Long newBundleLineItemId : bundleLineItemIds) {
//                            BundleLineItemEntity updatedBundleLineItem = bundleLineItemEntitySessionBeanLocal.retrieveBundleLineItemByBundleLineItemId(newBundleLineItemId);
//                            bundleEntityToUpdate.addBundleLineItem(updatedBundleLineItem);
//                        }
//                    }

                    bundleEntityToUpdate.setName(bundleEntity.getName());
                    bundleEntityToUpdate.setDescription(bundleEntity.getDescription());
                    bundleEntityToUpdate.setUnitPrice(bundleEntity.getUnitPrice());
                    // Removed in v5.0
                    //productEntityToUpdate.setCategory(bundleEntity.getCategory());
                    // Added in v5.1
                    bundleEntityToUpdate.setBundleRating((bundleEntity.getBundleRating()));
                } else {
                    throw new UpdateBundleException("SKU Code of bundle record to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new BundleNotFoundException("Bundle ID not provided for bundle to be updated");
        }
    }

    @Override
    public void deleteBundle(Long bundleId) throws BundleNotFoundException, DeleteBundleException {
        BundleEntity bundleEntityToRemove = retrieveBundleByBundleId(bundleId);

        //to be deleted when salesTransactionLineItem is added.
        entityManager.remove(bundleEntityToRemove);

//        List<SaleTransactionLineItemEntity> saleTransactionLineItemEntities = saleTransactionEntitySessionBeanLocal.retrieveSaleTransactionLineItemsByProductId(productId);
//        if(saleTransactionLineItemEntities.isEmpty())
//        {
//            entityManager.remove(productEntityToRemove);
//        }
//        else
//        {
//            throw new DeleteProductException("Product ID " + productId + " is associated with existing sale transaction line item(s) and cannot be deleted!");
//        }
    }
    // Added in v4.1

    @Override
    public void debitQuantityOnHand(Long bundleId, Integer quantityToDebit) throws BundleNotFoundException, BundleInsufficientQuantityOnHandException, ProductNotFoundException, ProductInsufficientQuantityOnHandException {
        BundleEntity bundleEntity = retrieveBundleByBundleId(bundleId);

        if (bundleEntity.getQuantityOnHand() >= quantityToDebit) {
            bundleEntity.setQuantityOnHand(bundleEntity.getQuantityOnHand() - quantityToDebit);
            for (BundleLineItemEntity bundleLineItem : bundleEntity.getBundleLineItems()) {
                ProductEntity productToDebit = bundleLineItem.getProductEntity();
                productEntitySessionBeanLocal.debitQuantityOnHand(productToDebit.getProductId(), quantityToDebit * bundleLineItem.getQuantity());
            }
        } else {
            throw new BundleInsufficientQuantityOnHandException("Bundle " + bundleEntity.getSkuCode() + " quantity on hand is " + bundleEntity.getQuantityOnHand() + " versus quantity to debit of " + quantityToDebit);
        }
    }

    // Added in v4.1
    @Override
    public void creditQuantityOnHand(Long bundleId, Integer quantityToCredit) throws ProductNotFoundException, BundleNotFoundException {

        BundleEntity bundleEntity = retrieveBundleByBundleId(bundleId);
        bundleEntity.setQuantityOnHand(bundleEntity.getQuantityOnHand() + quantityToCredit);

        for (BundleLineItemEntity bundleLineItem : bundleEntity.getBundleLineItems()) {
            ProductEntity productToCredit = bundleLineItem.getProductEntity();
            productEntitySessionBeanLocal.creditQuantityOnHand(productToCredit.getProductId(), quantityToCredit * bundleLineItem.getQuantity());
        }
    }

}
