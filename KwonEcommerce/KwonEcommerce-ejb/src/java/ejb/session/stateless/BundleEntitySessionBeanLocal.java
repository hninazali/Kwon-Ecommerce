/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BundleEntity;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.ejb.Local;
import javax.validation.ConstraintViolation;
import util.exception.BundleInsufficientQuantityOnHandException;
import util.exception.BundleNotFoundException;
import util.exception.BundleSkuCodeExistException;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewBundleException;
import util.exception.DeleteBundleException;
import util.exception.InputDataValidationException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.TagNotFoundException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateBundleException;

/**
 *
 * @author hninazali
 */
@Local
public interface BundleEntitySessionBeanLocal {
    
    public BundleEntity createNewBundle(BundleEntity newBundleEntity, List<Long> tagIds) throws BundleSkuCodeExistException, UnknownPersistenceException, InputDataValidationException, CreateNewBundleException, ProductNotFoundException, CategoryNotFoundException;
    public List<BundleEntity> retrieveAllBundles();
    public List<BundleEntity> searchBundlesByName(String searchString);
    public List<BundleEntity> filterBundlesByTags(List<Long> tagIds, String condition);
    public List<BundleEntity> filterBundlesByPrice(BigDecimal startPrice, BigDecimal endPrice);
    public BundleEntity retrieveBundleByBundleId(Long bundleId) throws BundleNotFoundException;
    public BundleEntity retrieveBundleByBundleSkuCode(String skuCode) throws BundleNotFoundException;
    public void updateBundle(BundleEntity bundleEntity, List<Long> tagIds) throws BundleNotFoundException, TagNotFoundException, UpdateBundleException, InputDataValidationException, ProductNotFoundException, CategoryNotFoundException;
    public void deleteBundle(Long bundleId) throws BundleNotFoundException, DeleteBundleException;
    public void debitQuantityOnHand(Long bundleId, Integer quantityToDebit) throws BundleNotFoundException, BundleInsufficientQuantityOnHandException, ProductNotFoundException, ProductInsufficientQuantityOnHandException;
    public void creditQuantityOnHand(Long bundleId, Integer quantityToCredit) throws ProductNotFoundException, BundleNotFoundException;



}
