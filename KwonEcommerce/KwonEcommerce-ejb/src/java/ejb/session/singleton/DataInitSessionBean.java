/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.AccountSessionBeanLocal;
import ejb.session.stateless.BrandEntitySessionBeanLocal;
import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.AccountEntity;
import entity.CategoryEntity;
import entity.ProductEntity;
import entity.StaffEntity;
import entity.TagEntity;
import entity.BrandEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.AccessRightEnum;
import util.exception.AccountNotFoundException;
import util.exception.AccountUsernameExistException;
import util.exception.BrandNotFoundException;
import util.exception.CreateNewBrandException;
import util.exception.CreateNewCategoryException;
import util.exception.CreateNewProductException;
import util.exception.CreateNewTagException;
import util.exception.InputDataValidationException;
import util.exception.ProductSkuCodeExistException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author winyfebriny
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB
    private AccountSessionBeanLocal accountSessionBeanLocal;
    
    @EJB
    private StaffEntitySessionBeanLocal staffEntitySessionBeanLocal;
    
    @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;

    @EJB
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;
    
    @EJB
    private BrandEntitySessionBeanLocal brandEntitySessionBeanLocal;
    
    public DataInitSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        try {
            accountSessionBeanLocal.retrieveAccountByUsername("account");
        } catch (AccountNotFoundException ex) {
            initializeData();
        }
    }

    private void initializeData() {
        try {
            accountSessionBeanLocal.createNewAccount(new AccountEntity("Default", "Account", AccessRightEnum.MANAGER, "account", "password", "account@gmail.com"));
            staffEntitySessionBeanLocal.createNewStaff(new StaffEntity("Default", "Account", AccessRightEnum.MANAGER, "manager", "password"));
            staffEntitySessionBeanLocal.createNewStaff(new StaffEntity("Default", "Staff One", AccessRightEnum.WAREHOUSESTAFF, "staff1", "password"));
            staffEntitySessionBeanLocal.createNewStaff(new StaffEntity("Default", "Staff Two", AccessRightEnum.WAREHOUSESTAFF, "staff2", "password"));
            
              
            // Added in v5.0
            // Updated in v5.1
            CategoryEntity makeUp = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Makeup", "Makeup"), null);
            CategoryEntity skinCare = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Skincare", "Skincare"), null);
            CategoryEntity personalHygiene = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Personal Hygiene", "Personal Hygiene"), null);
            CategoryEntity primer = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Primer", "Primer"), makeUp.getCategoryId());
            CategoryEntity foundation = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Foundation", "Foundation"), makeUp.getCategoryId());
            CategoryEntity lipstick = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Lipstick", "Lipstick"), makeUp.getCategoryId());
            CategoryEntity shampoo = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Shampoo", "Shampoo"), personalHygiene.getCategoryId());
            CategoryEntity bodyWash = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Body Wash", "Body Wash"), personalHygiene.getCategoryId());
            CategoryEntity lotions = categoryEntitySessionBeanLocal.createNewCategoryEntity(new CategoryEntity("Lotions", "Lotions"), skinCare.getCategoryId());
            
            // Newly added in v5.1
            TagEntity tagEntityPopular = tagEntitySessionBeanLocal.createNewTagEntity(new TagEntity("popular"));
            TagEntity tagEntityDiscount = tagEntitySessionBeanLocal.createNewTagEntity(new TagEntity("discount"));
            TagEntity tagEntityNew = tagEntitySessionBeanLocal.createNewTagEntity(new TagEntity("new"));
            
            BrandEntity innisfree = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("Innisfree"));

            // Newly added in v5.1
            List<Long> tagIdsPopular = new ArrayList<>();
            tagIdsPopular.add(tagEntityPopular.getTagId());
            
            List<Long> tagIdsDiscount = new ArrayList<>();
            tagIdsDiscount.add(tagEntityDiscount.getTagId());
            
            List<Long> tagIdsPopularDiscount = new ArrayList<>();
            tagIdsPopularDiscount.add(tagEntityPopular.getTagId());
            tagIdsPopularDiscount.add(tagEntityDiscount.getTagId());
            
            List<Long> tagIdsPopularNew = new ArrayList<>();
            tagIdsPopularNew.add(tagEntityPopular.getTagId());
            tagIdsPopularNew.add(tagEntityNew.getTagId());
            
            List<Long> tagIdsPopularDiscountNew = new ArrayList<>();
            tagIdsPopularDiscountNew.add(tagEntityPopular.getTagId());
            tagIdsPopularDiscountNew.add(tagEntityDiscount.getTagId());
            tagIdsPopularDiscountNew.add(tagEntityNew.getTagId());
            
            List<Long> tagIdsEmpty = new ArrayList<>();

            // Updated in v5.0
            // Updated in v5.1
            
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD001", "Innisfree Primer", "Best primer ever!! Fills up n blur out the pores.", 100, 10, new BigDecimal("10.00"), 1), primer.getCategoryId(), tagIdsPopular, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD002", "Laneige Primer", "For best whitening effect", 100, 10, new BigDecimal("25.50"), 2), primer.getCategoryId(), tagIdsDiscount, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD003", "Etude House Foundation", "Become a princess!", 100, 10, new BigDecimal("20.00"), 1), foundation.getCategoryId(), tagIdsPopularNew, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD004", "THEFACESHOP Foundation", "For best whitening effect", 100, 10, new BigDecimal("10.00"), 2), foundation.getCategoryId(), tagIdsPopularDiscountNew, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD005", "Innisfree Lipstick", "Most unique color", 100, 10, new BigDecimal("35.00"), 1), lipstick.getCategoryId(), tagIdsPopular, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD006", "COSRX Lipstick", "Boost your confidence!", 100, 10, new BigDecimal("20.05"), 2), lipstick.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
            
            // Added in v5.0
            // Updated in v5.1
            
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD07", "Moonshot Shampoo", "Moonshot Shampoo", 100, 10, new BigDecimal("20.00"), 3), shampoo.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD08", "The Saem Shampoo", "The Saem Shampoo", 100, 10, new BigDecimal("30.50"), 4), shampoo.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD09", "Skin Food Bodywash", "Skin Food Bodywash", 100, 10, new BigDecimal("50.00"), 3), bodyWash.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD010", "Dr Jart+ Bodywash", "Dr Jart+ Bodywash", 100, 10, new BigDecimal("100.00"), 4), bodyWash.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD011", "VT Cosmetics Lotion", "VT Cosmetics Lotion", 100, 10, new BigDecimal("95.00"), 3), lotions.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD012", "CandyLab Lotion", "CandyLab Lotion", 100, 10, new BigDecimal("19.05"), 4), lotions.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
           

        } catch (AccountUsernameExistException | StaffUsernameExistException | UnknownPersistenceException | InputDataValidationException| ProductSkuCodeExistException | CreateNewCategoryException | CreateNewTagException | CreateNewProductException| CreateNewBrandException | BrandNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
