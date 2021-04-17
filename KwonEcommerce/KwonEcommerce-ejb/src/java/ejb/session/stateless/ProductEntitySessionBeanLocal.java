package ejb.session.stateless;

import entity.ProductEntity;
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.Local;
import util.exception.BrandNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewBrandException;
import util.exception.CreateNewProductException;
import util.exception.CustomerNotFoundException;
import util.exception.DeleteProductException;
import util.exception.InputDataValidationException;
import util.exception.OrderRequestNotFoundException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateProductException;



@Local

public interface ProductEntitySessionBeanLocal
{
    public ProductEntity createNewProduct(ProductEntity newProductEntity, Long categoryId, List<Long> tagIds, Long brandId) throws ProductSkuCodeExistException, UnknownPersistenceException, InputDataValidationException, CreateNewProductException, CreateNewBrandException, BrandNotFoundException;

    List<ProductEntity> retrieveAllProducts();
    
    public List<ProductEntity> searchProductsByName(String searchString);
    
    public List<ProductEntity> filterProductsByCategory(Long categoryId) throws CategoryNotFoundException;
    
    public List<ProductEntity> filterProductsByTags(List<Long> tagIds, String condition);

    ProductEntity retrieveProductByProductId(Long productId) throws ProductNotFoundException;

    ProductEntity retrieveProductByProductSkuCode(String skuCode) throws ProductNotFoundException;

    public void updateProduct(ProductEntity productEntity, Long categoryId, List<Long> tagIds, Long brandId) throws ProductNotFoundException, CategoryNotFoundException, TagNotFoundException, BrandNotFoundException, UpdateProductException, InputDataValidationException;
    
    void deleteProduct(Long productId) throws ProductNotFoundException, DeleteProductException;
    
    void debitQuantityOnHand(Long productId, Integer quantityToDebit) throws ProductNotFoundException, ProductInsufficientQuantityOnHandException;
    
    void creditQuantityOnHand(Long productId, Integer quantityToCredit) throws ProductNotFoundException;

    public List<ProductEntity> filterProductsByBrand(Long brandId) throws BrandNotFoundException;

    public List<ProductEntity> filterProductsByPrice(BigDecimal startPrice, BigDecimal endPrice);

    public void orderProduct(long productId, int quantityToOrder) throws ProductNotFoundException;

    public void approveOrder(long requestId) throws OrderRequestNotFoundException, ProductNotFoundException;

    public List<ProductEntity> filterProductsByTag(Long tagId) throws TagNotFoundException;

    public List<ProductEntity> retrieveRecommendations(Long customerId) throws CustomerNotFoundException, CategoryNotFoundException;
}
