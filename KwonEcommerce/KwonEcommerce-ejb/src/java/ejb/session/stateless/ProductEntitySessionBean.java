package ejb.session.stateless;

import entity.CategoryEntity;
import entity.ProductEntity;
import entity.SaleTransactionLineItemEntity;
import entity.TagEntity;
import entity.BrandEntity;
import entity.CustomerEntity;
import entity.OrderLineItemEntity;
import entity.OrderRequestEntity;
import entity.SupplierEntity;
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
import util.exception.BrandNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewProductException;
import util.exception.DeleteProductException;
import util.exception.CreateNewBrandException;
import util.exception.CreateNewProductException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.OrderRequestNotFoundException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateProductException;



@Stateless

public class ProductEntitySessionBean implements ProductEntitySessionBeanLocal
{

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager entityManager;
    
    // Added in v5.0
    @EJB
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    // Added in v5.1
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;
//    @EJB
//    private SaleTransactionEntitySessionBeanLocal saleTransactionEntitySessionBeanLocal;
    @EJB
    private OrderTransactionSessionBeanLocal orderTransactionSessionBeanLocal;
    
    @EJB
    private BrandEntitySessionBeanLocal brandEntitySessionBeanLocal;
    
    // Added in v4.2 for bean validation
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    
    
    public ProductEntitySessionBean()
    {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    
    
    // Updated in v4.1
    // Updated in v4.2 with bean validation
    // Updated in v5.0 to include association with new category entity
    // Updated in v5.1 with category entity and tag entity processing
    
    @Override
    public ProductEntity createNewProduct(ProductEntity newProductEntity, Long categoryId, List<Long> tagIds, Long brandId) throws ProductSkuCodeExistException, UnknownPersistenceException, InputDataValidationException, CreateNewProductException, CreateNewBrandException, BrandNotFoundException
    {
        Set<ConstraintViolation<ProductEntity>>constraintViolations = validator.validate(newProductEntity);
        
        if(constraintViolations.isEmpty())
        {
            try
            {
                if(categoryId == null)
                {
                    throw new CreateNewProductException("The new product must be associated a leaf category");
                }
                
                CategoryEntity categoryEntity = categoryEntitySessionBeanLocal.retrieveCategoryByCategoryId(categoryId);
                
                if(brandId ==null){
                    throw new CreateNewBrandException("Error occured with creating new brand");
                }
                
                BrandEntity brandEntity = brandEntitySessionBeanLocal.retrieveBrandByBrandId(brandId);
                
                if(!categoryEntity.getSubCategoryEntities().isEmpty())
                {
                    throw new CreateNewProductException("Selected category for the new product is not a leaf category");
                }
                
                entityManager.persist(newProductEntity);
                newProductEntity.setCategoryEntity(categoryEntity);
                newProductEntity.setBrandEntity(brandEntity);
                
                if(tagIds != null && (!tagIds.isEmpty()))
                {
                    for(Long tagId:tagIds)
                    {
                        TagEntity tagEntity = tagEntitySessionBeanLocal.retrieveTagByTagId(tagId);
                        newProductEntity.addTag(tagEntity);
                    }
                }
                
                entityManager.flush();

                return newProductEntity;
            }
            catch(PersistenceException ex)
            {
                if(ex.getCause() != null && ex.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException"))
                {
                    if(ex.getCause().getCause() != null && ex.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException"))
                    {
                        throw new ProductSkuCodeExistException();
                    }
                    else
                    {
                        throw new UnknownPersistenceException(ex.getMessage());
                    }
                }
                else
                {
                    throw new UnknownPersistenceException(ex.getMessage());
                }
            }
            catch(CategoryNotFoundException | TagNotFoundException ex)
            {
                throw new CreateNewProductException("An error has occurred while creating the new product: " + ex.getMessage());
            }
        }
        else
        {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
    }
    
    
    
    @Override
    public List<ProductEntity> retrieveAllProducts()
    {
        Query query = entityManager.createQuery("SELECT p FROM ProductEntity p ORDER BY p.skuCode ASC");        
        List<ProductEntity> productEntities = query.getResultList();
        
        for(ProductEntity productEntity:productEntities)
        {
            productEntity.getCategoryEntity();
            productEntity.getTagEntities().size();
            productEntity.getBrandEntity();
        }
        
        return productEntities;
    }
    
    @Override
    public List<ProductEntity> retrieveRecommendations(Long customerId) throws CustomerNotFoundException, CategoryNotFoundException
    {
        CustomerEntity customer = customerSessionBeanLocal.retrieveCustomerById(customerId);
        List<OrderLineItemEntity> lineItems = customer.getOrderLineItemEntities();
        List<ProductEntity> products = new ArrayList<>();
        CategoryEntity tempCategory = new CategoryEntity();
        ArrayList<CategoryEntity> categories = new ArrayList<>();
        
        for (OrderLineItemEntity tempLineItem : lineItems)
        {
            if (tempLineItem.getProductEntity() != null)
            {
                tempCategory = tempLineItem.getProductEntity().getCategoryEntity();
                
                if (! categories.contains(tempCategory))
                {
                    categories.add(tempCategory);
                    products.addAll(this.filterProductsByCategory(tempCategory.getCategoryId()));
                }
            }
            else
            {
                for (CategoryEntity category : tempLineItem.getBundleEntity().getCategoryEntities())
                {
                    tempCategory = category;
                    
                    if (! categories.contains(tempCategory))
                    {
                        categories.add(tempCategory);
                        products.addAll(this.filterProductsByCategory(tempCategory.getCategoryId()));
                    }
                }
            }
        }
        
        return products;
    }
    
    
    
    // Newly addded in v5.1
    
    @Override
    public List<ProductEntity> searchProductsByName(String searchString)
    {
        Query query = entityManager.createQuery("SELECT p FROM ProductEntity p WHERE p.name LIKE :inSearchString ORDER BY p.skuCode ASC");
        query.setParameter("inSearchString", "%" + searchString + "%");
        List<ProductEntity> productEntities = query.getResultList();
        
        for(ProductEntity productEntity:productEntities)
        {
            productEntity.getCategoryEntity();
            productEntity.getTagEntities().size();
            productEntity.getBrandEntity();
        }
        
        return productEntities;
    }
    
    
    
    // Newly addded in v5.1
    
    @Override
    public List<ProductEntity> filterProductsByCategory(Long categoryId) throws CategoryNotFoundException
    {
        List<ProductEntity> productEntities = new ArrayList<>();
        CategoryEntity categoryEntity = categoryEntitySessionBeanLocal.retrieveCategoryByCategoryId(categoryId);
        
        if(categoryEntity.getSubCategoryEntities().isEmpty())
        {
            productEntities = categoryEntity.getProductEntities(); 
        }
        else
        {
            for(CategoryEntity subCategoryEntity:categoryEntity.getSubCategoryEntities())
            {
                productEntities.addAll(addSubCategoryProducts(subCategoryEntity));
            }
        }

        //roductEntities = categoryEntity.getProductEntities(); 
        
        for(ProductEntity productEntity:productEntities)
        {
            productEntity.getCategoryEntity();
            productEntity.getBrandEntity();
            productEntity.getTagEntities().size();
        }
        
//        Collections.sort(productEntities, new Comparator<ProductEntity>()
//            {
//                public int compare(ProductEntity pe1, ProductEntity pe2) {
//                    return pe1.getSkuCode().compareTo(pe2.getSkuCode());
//                }
//            });

        return productEntities;
    }
    
    @Override
    public List<ProductEntity> filterProductsByTag(Long tagId) throws TagNotFoundException
    {
        List<ProductEntity> productEntities = new ArrayList<>();
        TagEntity tag = tagEntitySessionBeanLocal.retrieveTagByTagId(tagId);
        
        productEntities = tag.getProductEntities();
        
        for(ProductEntity productEntity:productEntities)
        {
            productEntity.getCategoryEntity();
            productEntity.getTagEntities().size();
            productEntity.getBrandEntity();
        }
        
//        Collections.sort(productEntities, new Comparator<ProductEntity>()
//            {
//                public int compare(ProductEntity pe1, ProductEntity pe2) {
//                    return pe1.getSkuCode().compareTo(pe2.getSkuCode());
//                }
//            });

        return productEntities;
    }
    
    @Override
    public List<ProductEntity> filterProductsByTags(List<Long> tagIds, String condition)
    {
        List<ProductEntity> productEntities = new ArrayList<>();
        
        if(tagIds == null || tagIds.isEmpty() || (!condition.equals("AND") && !condition.equals("OR")))
        {
            return productEntities;
        }
        else
        {
            if(condition.equals("OR"))
            {
                Query query = entityManager.createQuery("SELECT DISTINCT pe FROM ProductEntity pe, IN (pe.tagEntities) te WHERE te.tagId IN :inTagIds ORDER BY pe.skuCode ASC");
                query.setParameter("inTagIds", tagIds);
                productEntities = query.getResultList();                                                          
            }
            else // AND
            {
                String selectClause = "SELECT pe FROM ProductEntity pe";
                String whereClause = "";
                Boolean firstTag = true;
                Integer tagCount = 1;

                for(Long tagId:tagIds)
                {
                    selectClause += ", IN (pe.tagEntities) te" + tagCount;

                    if(firstTag)
                    {
                        whereClause = "WHERE te1.tagId = " + tagId;
                        firstTag = false;
                    }
                    else
                    {
                        whereClause += " AND te" + tagCount + ".tagId = " + tagId; 
                    }
                    
                    tagCount++;
                }
                
                String jpql = selectClause + " " + whereClause + " ORDER BY pe.skuCode ASC";
                Query query = entityManager.createQuery(jpql);
                productEntities = query.getResultList();                                
            }
            
            for(ProductEntity productEntity:productEntities)
            {
                productEntity.getCategoryEntity();
                productEntity.getTagEntities().size();
                productEntity.getBrandEntity();
            }
            
            Collections.sort(productEntities, new Comparator<ProductEntity>()
            {
                public int compare(ProductEntity pe1, ProductEntity pe2) {
                    return pe1.getSkuCode().compareTo(pe2.getSkuCode());
                }
            });
            
            return productEntities;
        }
    }
    
    @Override
    public List<ProductEntity> filterProductsByBrand(Long brandId) throws BrandNotFoundException
    {
        List<ProductEntity> productEntities = new ArrayList<>();
        BrandEntity brandEntity = brandEntitySessionBeanLocal.retrieveBrandByBrandId(brandId);
 
        productEntities = brandEntity.getProductEntities();
        
        for(ProductEntity productEntity:productEntities)
        {
            productEntity.getCategoryEntity();
            productEntity.getBrandEntity();
            productEntity.getTagEntities().size();
        }
        
//        Collections.sort(productEntities, new Comparator<ProductEntity>()
//            {
//                public int compare(ProductEntity pe1, ProductEntity pe2) {
//                    return pe1.getSkuCode().compareTo(pe2.getSkuCode());
//                }
//            });

        return productEntities;
    }
    
    @Override
    public List<ProductEntity> filterProductsByPrice(BigDecimal startPrice, BigDecimal endPrice)
    {
        List<ProductEntity> productEntities = new ArrayList<>();
       
        Query query = entityManager.createQuery("SELECT DISTINCT pe FROM ProductEntity pe WHERE pe.unitPrice BETWEEN :inStartPrice AND :inEndPrice ORDER BY pe.skuCode ASC");
        query.setParameter("inStartPrice", startPrice);
        query.setParameter("inEndPrice", endPrice);
        productEntities = query.getResultList();
        
        for(ProductEntity productEntity:productEntities)
        {
            productEntity.getCategoryEntity();
            productEntity.getBrandEntity();
            productEntity.getTagEntities().size();
        }

        return productEntities;
    }
    
    @Override
    public ProductEntity retrieveProductByProductId(Long productId) throws ProductNotFoundException
    {
        ProductEntity productEntity = entityManager.find(ProductEntity.class, productId);
        
        if(productEntity != null)
        {
            productEntity.getCategoryEntity();
            productEntity.getTagEntities().size();
            
            return productEntity;
        }
        else
        {
            throw new ProductNotFoundException("Product ID " + productId + " does not exist!");
        }               
    }
    
    
    
    @Override
    public ProductEntity retrieveProductByProductSkuCode(String skuCode) throws ProductNotFoundException
    {
        Query query = entityManager.createQuery("SELECT p FROM ProductEntity p WHERE p.skuCode = :inSkuCode");
        query.setParameter("inSkuCode", skuCode);
        
        try
        {
            ProductEntity productEntity = (ProductEntity)query.getSingleResult();            
            productEntity.getCategoryEntity();
            productEntity.getTagEntities().size();
            
            return productEntity;
        }
        catch(NoResultException | NonUniqueResultException ex)
        {
            throw new ProductNotFoundException("Sku Code " + skuCode + " does not exist!");
        }
    }
    
    
    
    // Updated in v4.1
    // Updated in v4.2 to include reorderQuantity and bean validation
    // Updated in v5.0 to include association with new category entity
    // Updated in v5.1 with category entity and tag entity processing
    
    @Override
    public void updateProduct(ProductEntity productEntity, Long categoryId, List<Long> tagIds,  Long brandId) throws ProductNotFoundException, CategoryNotFoundException, TagNotFoundException, BrandNotFoundException, UpdateProductException, InputDataValidationException
    {
        if(productEntity != null && productEntity.getProductId()!= null)
        {
            Set<ConstraintViolation<ProductEntity>>constraintViolations = validator.validate(productEntity);
        
            if(constraintViolations.isEmpty())
            {
                ProductEntity productEntityToUpdate = retrieveProductByProductId(productEntity.getProductId());

                if(productEntityToUpdate.getSkuCode().equals(productEntity.getSkuCode()))
                {
                    // Added in v5.1
                    if(categoryId != null && (!productEntityToUpdate.getCategoryEntity().getCategoryId().equals(categoryId)))
                    {
                        CategoryEntity categoryEntityToUpdate = categoryEntitySessionBeanLocal.retrieveCategoryByCategoryId(categoryId);
                        
                        if(!categoryEntityToUpdate.getSubCategoryEntities().isEmpty())
                        {
                            throw new UpdateProductException("Selected category for the new product is not a leaf category");
                        }
                        
                        productEntityToUpdate.setCategoryEntity(categoryEntityToUpdate);
                    }
                    
                    if(brandId != null && (!productEntityToUpdate.getBrandEntity().getBrandId().equals(brandId))){
                        BrandEntity brandEntity = brandEntitySessionBeanLocal.retrieveBrandByBrandId(brandId);
                        productEntityToUpdate.setBrandEntity(brandEntity);
           
                    }
                    
                    // Added in v5.1
                    if(tagIds != null)
                    {
                        for(TagEntity tagEntity:productEntityToUpdate.getTagEntities())
                        {
                            tagEntity.getProductEntities().remove(productEntityToUpdate);
                        }
                        
                        productEntityToUpdate.getTagEntities().clear();
                        
                        for(Long tagId:tagIds)
                        {
                            TagEntity tagEntity = tagEntitySessionBeanLocal.retrieveTagByTagId(tagId);
                            productEntityToUpdate.addTag(tagEntity);
                        }
                    }
                    
                    productEntityToUpdate.setName(productEntity.getName());
                    productEntityToUpdate.setDescription(productEntity.getDescription());
                    productEntityToUpdate.setQuantityOnHand(productEntity.getQuantityOnHand());
                    productEntityToUpdate.setReorderQuantity(productEntity.getReorderQuantity());
                    productEntityToUpdate.setUnitPrice(productEntity.getUnitPrice());
                
                    // Removed in v5.0
                    //productEntityToUpdate.setCategory(productEntity.getCategory());
                    // Added in v5.1
                    productEntityToUpdate.setProductRating((productEntity.getProductRating()));
                }
                else
                {
                    throw new UpdateProductException("SKU Code of product record to be updated does not match the existing record");
                }
            }
            else
            {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        }
        else
        {
            throw new ProductNotFoundException("Product ID not provided for product to be updated");
        }
    }
    
    
    
    // Updated in v4.1
    
    @Override
    public void deleteProduct(Long productId) throws ProductNotFoundException, DeleteProductException
    {
        ProductEntity productEntityToRemove = retrieveProductByProductId(productId);
        if (productEntityToRemove != null)
        {
        
        productEntityToRemove.getCategoryEntity().getProductEntities().remove(productEntityToRemove);
        productEntityToRemove.getBrandEntity().getProductEntities().remove(productEntityToRemove);
        
        for (TagEntity tag : productEntityToRemove.getTagEntities())
        {
            tag.getProductEntities().remove(productEntityToRemove);
        }
        
        //to be deleted when salesTransactionLineItem is added 
        entityManager.remove(productEntityToRemove);

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
        else
        {
            throw new DeleteProductException("Product ID " + productId + " is associated with existing order line item(s) and cannot be deleted!");
        }
    }
    
    
    
    // Added in v4.1
    
    @Override
    public void debitQuantityOnHand(Long productId, Integer quantityToDebit) throws ProductNotFoundException, ProductInsufficientQuantityOnHandException
    {
        ProductEntity productEntity = retrieveProductByProductId(productId);
        
        if(productEntity.getQuantityOnHand() >= quantityToDebit)
        {
            productEntity.setQuantityOnHand(productEntity.getQuantityOnHand() - quantityToDebit);
        }
        else
        {
            throw new ProductInsufficientQuantityOnHandException("Product " + productEntity.getSkuCode() + " quantity on hand is " + productEntity.getQuantityOnHand() + " versus quantity to debit of " + quantityToDebit);
        }
    }
    
    
    
    // Added in v4.1
    
    @Override
    public void creditQuantityOnHand(Long productId, Integer quantityToCredit) throws ProductNotFoundException
    {
        ProductEntity productEntity = retrieveProductByProductId(productId);
        productEntity.setQuantityOnHand(productEntity.getQuantityOnHand() + quantityToCredit);
    }
    
    @Override
    public void orderProduct(long productId, int quantityToOrder) throws ProductNotFoundException {
        ProductEntity product = this.retrieveProductByProductId(productId);
        OrderRequestEntity orderRequest = new OrderRequestEntity(false, quantityToOrder);
        entityManager.persist(orderRequest);
        orderRequest.setProduct(product);
        SupplierEntity supplier = product.getBrandEntity().getSupplier();
        orderRequest.setSupplier(supplier);
    }
    
    @Override
    public void approveOrder(long requestId) throws OrderRequestNotFoundException, ProductNotFoundException 
    {
        OrderRequestEntity orderRequest = entityManager.find(OrderRequestEntity.class, requestId);
        
        if(orderRequest != null)
        {
            orderRequest.setIsApproved(true);
            ProductEntity temp = orderRequest.getProduct();
            ProductEntity product = this.retrieveProductByProductId(temp.getProductId());
            int currentQtyOnHand = product.getQuantityOnHand();
            product.setQuantityOnHand(currentQtyOnHand + orderRequest.getQuantityOrdered());
        }
        else
        {
            throw new OrderRequestNotFoundException("Order Request Id ID " + requestId + " does not exist!");
        }
    }
    
    
    // Newly added in v5.1
    
    private List<ProductEntity> addSubCategoryProducts(CategoryEntity categoryEntity)
    {
        List<ProductEntity> productEntities = new ArrayList<>();
                
        if(categoryEntity.getSubCategoryEntities().isEmpty())
        {
            return categoryEntity.getProductEntities();
        }
        else
        {
            for(CategoryEntity subCategoryEntity:categoryEntity.getSubCategoryEntities())
            {
                productEntities.addAll(addSubCategoryProducts(subCategoryEntity));
            }
            
            return productEntities;
        }
    }
    
    
    
    // Added in v4.2
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ProductEntity>>constraintViolations)
    {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations)
        {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
