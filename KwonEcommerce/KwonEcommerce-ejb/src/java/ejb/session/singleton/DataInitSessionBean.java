/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.BrandEntitySessionBeanLocal;
import ejb.session.stateless.BundleEntitySessionBeanLocal;
import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.CreditCardSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.PersonalCartSessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.StaffEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.CategoryEntity;
import entity.ProductEntity;
import entity.StaffEntity;
import entity.TagEntity;
import entity.BrandEntity;
import entity.BundleEntity;
import entity.BundleLineItemEntity;
import entity.CreditCardEntity;
import entity.CustomerEntity;
import entity.PersonalCartEntity;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import util.enumeration.AccessRightEnum;
import util.exception.BrandNotFoundException;
import util.exception.BundleSkuCodeExistException;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewBrandException;
import util.exception.CreateNewBundleException;
import util.exception.CreateNewCategoryException;
import util.exception.CreateNewCreditCardException;
import util.exception.CreateNewPersonalCartException;
import util.exception.CreateNewProductException;
import util.exception.CreateNewTagException;
import util.exception.CustomerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.StaffNotFoundException;
import util.exception.StaffUsernameExistException;
import util.exception.UnknownPersistenceException;

@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB(name = "CreditCardSessionBeanLocal")
    private CreditCardSessionBeanLocal creditCardSessionBeanLocal;

    @EJB(name = "PersonalCartSessionBeanLocal")
    private PersonalCartSessionBeanLocal personalCartSessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;
    
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
    
    @EJB
    private BundleEntitySessionBeanLocal bundleEntitySessionBeanLocal;
    
    
    
    public DataInitSessionBean() {
    }

    @PostConstruct
    public void postConstruct() {
        try {
            staffEntitySessionBeanLocal.retrieveStaffByUsername("manager");
        } catch (StaffNotFoundException ex) {
            initializeData();
        }
    }

    private void initializeData() {
        try {
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
            BrandEntity laneige = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("Laneige"));
            BrandEntity etudeHouse = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("Etude House"));
            BrandEntity faceShop = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("THE FACESHOP"));
            BrandEntity cosrx = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("COSRX"));
            BrandEntity moonshot = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("Moonshot"));
            BrandEntity theSaem = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("The Saem"));
            BrandEntity skinFood = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("Skin Food"));
            BrandEntity drJart = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("Dr Jart+"));
            BrandEntity vtCosmetics = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("VT Cosmetics"));
            BrandEntity candyLab = brandEntitySessionBeanLocal.createNewBrandEntity(new BrandEntity("CandyLab"));
            
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
            
            ProductEntity product1 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD001", "Innisfree Primer", "Best primer ever!! Fills up n blur out the pores.", 100, 10, new BigDecimal("10.00"), 1), primer.getCategoryId(), tagIdsPopular, innisfree.getBrandId());
            ProductEntity product2 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD002", "Laneige Primer", "For best whitening effect", 100, 10, new BigDecimal("25.50"), 2), primer.getCategoryId(), tagIdsDiscount, laneige.getBrandId());
            ProductEntity product3 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD003", "Etude House Foundation", "Become a princess!", 100, 10, new BigDecimal("20.00"), 1), foundation.getCategoryId(), tagIdsPopularNew, etudeHouse.getBrandId());
            ProductEntity product4 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD004", "THEFACESHOP Foundation", "For best whitening effect", 100, 10, new BigDecimal("10.00"), 2), foundation.getCategoryId(), tagIdsPopularDiscountNew, faceShop.getBrandId());
            ProductEntity product5 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD005", "Innisfree Lipstick", "Most unique color", 100, 10, new BigDecimal("35.00"), 1), lipstick.getCategoryId(), tagIdsPopular, innisfree.getBrandId());
            ProductEntity product6 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD006", "COSRX Lipstick", "Boost your confidence!", 100, 10, new BigDecimal("20.05"), 2), lipstick.getCategoryId(), tagIdsEmpty, cosrx.getBrandId());
            
            // Added in v5.0
            // Updated in v5.1
            
            ProductEntity product7 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD007", "Moonshot Shampoo", "Moonshot Shampoo", 100, 10, new BigDecimal("20.00"), 3), shampoo.getCategoryId(), tagIdsEmpty, moonshot.getBrandId());
            ProductEntity product8 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD008", "The Saem Shampoo", "The Saem Shampoo", 100, 10, new BigDecimal("30.50"), 4), shampoo.getCategoryId(), tagIdsEmpty, theSaem.getBrandId());
            ProductEntity product9 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD010", "Dr Jart+ Bodywash", "Dr Jart+ Bodywash", 100, 10, new BigDecimal("100.00"), 4), bodyWash.getCategoryId(), tagIdsEmpty, drJart.getBrandId());
            ProductEntity product10 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD011", "VT Cosmetics Lotion", "VT Cosmetics Lotion", 100, 10, new BigDecimal("95.00"), 3), lotions.getCategoryId(), tagIdsEmpty, vtCosmetics.getBrandId());
            ProductEntity product11 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD012", "CandyLab Lotion", "CandyLab Lotion", 100, 10, new BigDecimal("19.05"), 4), lotions.getCategoryId(), tagIdsEmpty, candyLab.getBrandId());
            
            
            ProductEntity product13 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD013", "Dr Jart+ Shampoo", "Dr Jart+ Shampoo", 100, 10, new BigDecimal("20.00"), 3), shampoo.getCategoryId(), tagIdsEmpty, drJart.getBrandId());
            ProductEntity product14 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD014", "CandyLab Shampoo", "CandyLab Shampoo", 100, 10, new BigDecimal("30.50"), 4), shampoo.getCategoryId(), tagIdsEmpty, candyLab.getBrandId());
            ProductEntity product15 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD015", "COSRX Bodywash", "COSRX Bodywash", 100, 10, new BigDecimal("100.00"), 4), bodyWash.getCategoryId(), tagIdsEmpty, cosrx.getBrandId());
            ProductEntity product16 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD016", "Moonshot Lotion", "Moonshot Lotion", 100, 10, new BigDecimal("95.00"), 3), lotions.getCategoryId(), tagIdsEmpty, moonshot.getBrandId());
            ProductEntity product17 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD017", "Laneige Lotion", "Laneige Lotion", 100, 10, new BigDecimal("19.05"), 4), lotions.getCategoryId(), tagIdsEmpty, laneige.getBrandId());
            
            
            ProductEntity product18 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD018", "CandyLab Primer", "Sweetest primer ever!!.", 100, 10, new BigDecimal("10.00"), 1), primer.getCategoryId(), tagIdsPopular, candyLab.getBrandId());
            ProductEntity product19 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD019", "Etude House Primer", "For best whitening effect", 100, 10, new BigDecimal("25.50"), 2), primer.getCategoryId(), tagIdsDiscount, etudeHouse.getBrandId());
            ProductEntity product20 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD020", "Laneige Foundation", "Become a princess!", 100, 10, new BigDecimal("20.00"), 1), foundation.getCategoryId(), tagIdsPopularNew, laneige.getBrandId());
            ProductEntity product21 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD021", "Innisfree Foundation", "For best whitening effect", 100, 10, new BigDecimal("10.00"), 2), foundation.getCategoryId(), tagIdsPopularDiscountNew, innisfree.getBrandId());
            ProductEntity product22 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD022", "Moonshot Lipstick", "Most unique color", 100, 10, new BigDecimal("35.00"), 1), lipstick.getCategoryId(), tagIdsPopular, moonshot.getBrandId());
            ProductEntity product23 = productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD023", "The Saem Lipstick", "Boost your confidence!", 100, 10, new BigDecimal("20.05"), 2), lipstick.getCategoryId(), tagIdsEmpty, theSaem.getBrandId());
            
            

//            // Updated in v5.0
//            // Updated in v5.1
//            
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD001", "Innisfree Primer", "Best primer ever!! Fills up n blur out the pores.", 100, 10, new BigDecimal("10.00"), 1, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fus.innisfree.com%2Fproducts%2Fpore-blur-primer&psig=AOvVaw3zMD3bPJJmhsFudqJ7XvAh&ust=1618114417815000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCKD5yoro8u8CFQAAAAAdAAAAABAK"), primer.getCategoryId(), tagIdsPopular, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD002", "Laneige Primer", "For best whitening effect", 100, 10, new BigDecimal("25.50"), 2, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.laneige.com%2Fint%2Fen%2Fmakeup%2Fwater-glow-base-corrector.html&psig=AOvVaw3i79RzJw1w9QXN9uabLw2p&ust=1618114453504000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNDrwJvo8u8CFQAAAAAdAAAAABAE"), primer.getCategoryId(), tagIdsDiscount, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD003", "Etude House Foundation", "Become a princess!", 100, 10, new BigDecimal("20.00"), 1, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fcosmetic-love.com%2Fproducts%2Fetude-house-double-lasting-foundation-new-30g&psig=AOvVaw0KIeDaPLDeNxYYTths1fTr&ust=1618114533851000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCMiF4MHo8u8CFQAAAAAdAAAAABAK"), foundation.getCategoryId(), tagIdsPopularNew, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD004", "THEFACESHOP Foundation", "For best whitening effect", 100, 10, new BigDecimal("10.00"), 2, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.koinshop.com%2Fproduct%2Fthe-face-shop-ink-lasting-foundation-slim-fit-bright-cream-beige%2F&psig=AOvVaw1xgfMDuwDHSAe1wyYMsDJ1&ust=1618114586307000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCMCLg9vo8u8CFQAAAAAdAAAAABAE"), foundation.getCategoryId(), tagIdsPopularDiscountNew, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD005", "Innisfree Lipstick", "Most unique color", 100, 10, new BigDecimal("35.00"), 1, "https://www.google.com/url?sa=i&url=http%3A%2F%2Fwww.innisfree.com%2Fsg%2Fen%2Fproduct%2FproductList.do%3FcatCd01%3DUB%26catCd02%3DUBBB%26skinType%3Dmb&psig=AOvVaw2PJaPeKZ8T8uR-wCrNnDY_&ust=1618114617804000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCPi0zero8u8CFQAAAAAdAAAAABAG"), lipstick.getCategoryId(), tagIdsPopular, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD006", "COSRX Lipstick", "Boost your confidence!", 100, 10, new BigDecimal("20.05"), 2, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fthebeautyjunkee.blogspot.com%2F2019%2F03%2Ffebruary-2019-beauty-favorites.html&psig=AOvVaw0lTK-lqIV7xsFRtulYlH7u&ust=1618114667903000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCPCzxaDp8u8CFQAAAAAdAAAAABAE"), lipstick.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
//            
//            // Added in v5.0
//            // Updated in v5.1
//            
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD007", "Moonshot Shampoo", "Moonshot Shampoo", 100, 10, new BigDecimal("20.00"), 3, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fficklebeauty.com%2Fproducts%2Fpyungkang-yul-ato-wash-shampoo-blue-label&psig=AOvVaw3O2eImyqb1TnJDDEhRghyk&ust=1618114903093000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCIC5i_Pp8u8CFQAAAAAdAAAAABAJ"), shampoo.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD008", "The Saem Shampoo", "The Saem Shampoo", 100, 10, new BigDecimal("30.50"), 4, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fbeautyboxkorea.com%2Fproduct%2Fthe-saem-silk-hair-refresh-shampoo-320ml%2F19536%2F&psig=AOvVaw3QmphpCsSVCr2npbmlMhEM&ust=1618114933189000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCLDUqYDq8u8CFQAAAAAdAAAAABAE"), shampoo.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD009", "Skin Food Bodywash", "Skin Food Bodywash", 100, 10, new BigDecimal("50.00"), 3, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.carousell.sg%2Fp%2Fskinfood-rose-tea-scented-body-wash-body-milk-335ml-114903087%2F&psig=AOvVaw1_naHXJOn4q73A19PKE173&ust=1618114966661000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCKCV5ZTq8u8CFQAAAAAdAAAAABAD"), bodyWash.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD010", "Dr Jart+ Bodywash", "Dr Jart+ Bodywash", 100, 10, new BigDecimal("100.00"), 4, "https://www.google.com/url?sa=i&url=http%3A%2F%2Fwww.stylekorean.com%2Fshop%2Fdr.jart-ceramidin-body-wash-250ml%2F1525315985%2F&psig=AOvVaw17iI3K7p3VN313-2TvKgcK&ust=1618115004293000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNDlm6Lq8u8CFQAAAAAdAAAAABAQ"), bodyWash.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD011", "VT Cosmetics Lotion", "VT Cosmetics Lotion", 100, 10, new BigDecimal("95.00"), 3, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fus.drjart.com%2Fproducts%2Fcica-cream&psig=AOvVaw3bxOoWf46Jo-b8ra14NPLU&ust=1618115127116000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCNi52dzq8u8CFQAAAAAdAAAAABAG"), lotions.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
//            productEntitySessionBeanLocal.createNewProduct(new ProductEntity("PROD012", "CandyLab Lotion", "CandyLab Lotion", 100, 10, new BigDecimal("19.05"), 4, "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.beautykoreamall.com%2Fproduct%2F%3FSM%3Dview%26prd_code%3D1608625290&psig=AOvVaw0Qa4briN0qUnH4UOotwvVt&ust=1618115158551000&source=images&cd=vfe&ved=0CAIQjRxqFwoTCKDx3-3q8u8CFQAAAAAdAAAAABAD"), lotions.getCategoryId(), tagIdsEmpty, innisfree.getBrandId());
//            
            PersonalCartEntity pc1 = personalCartSessionBeanLocal.createPersonalCartEntity(new PersonalCartEntity());
            PersonalCartEntity pc2 = personalCartSessionBeanLocal.createPersonalCartEntity(new PersonalCartEntity());
            PersonalCartEntity pc3 = personalCartSessionBeanLocal.createPersonalCartEntity(new PersonalCartEntity());
            
            customerSessionBeanLocal.createNewCustomer(new CustomerEntity("Customer", "One", "CustomerOne", "customerone@gmail.com", "password", "NUS RVRC RVR F #07-722", "119081", pc1, false));
            customerSessionBeanLocal.createNewCustomer(new CustomerEntity("Customer", "Two", "CustomerTwo", "customertwo@gmail.com", "password", "NUS Cinnamon #09-344", "138593", pc2, false));
            customerSessionBeanLocal.createNewCustomer(new CustomerEntity("Customer", "Three", "CustomerThree", "customerthree@gmail.com", "password", "NUS Tembusu #05-233", "138598", pc3, false));
            
            CreditCardEntity cc1 = new CreditCardEntity("1234567891011121", "Personal DBS Card");
            creditCardSessionBeanLocal.createNewCreditCard(cc1);
            
            BundleEntity newBundle = new BundleEntity();
            
            BundleLineItemEntity lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product1);
            lineItem.setQuantity(1);
            BigDecimal newQtyToAdd = new BigDecimal(1);
            BigDecimal newSubtotal = product1.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product2);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product2.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product3);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product3.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            newBundle.setSkuCode("BUND001");
            newBundle.setName("Primer Foundation Set");
            newBundle.setDescription("Best primer and foundation set discounted for a limited time only!");
            newBundle.setQuantityOnHand(10);
            newBundle.setReorderQuantity(10);
            newBundle.setUnitPrice(new BigDecimal("50"));
            newBundle.setBundleRating(3);

            BundleEntity newBundleEntity1 = bundleEntitySessionBeanLocal.createNewBundle(newBundle, tagIdsDiscount);
            
            newBundle = new BundleEntity();
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product4);
            lineItem.setQuantity(2);
            newQtyToAdd = new BigDecimal(2);
            newSubtotal = product4.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product5);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product5.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product6);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product6.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            newBundle.setSkuCode("BUND002");
            newBundle.setName("Foundation Lipstick Set");
            newBundle.setDescription("New foundation lipstick set selling out fast!");
            newBundle.setQuantityOnHand(10);
            newBundle.setReorderQuantity(30);
            newBundle.setUnitPrice(new BigDecimal("75"));
            newBundle.setBundleRating(4);

            BundleEntity newBundleEntity2 = bundleEntitySessionBeanLocal.createNewBundle(newBundle, tagIdsPopularNew);
            
            newBundle = new BundleEntity();
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product7);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product7.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product8);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product8.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product9);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product9.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product10);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product10.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            lineItem = new BundleLineItemEntity();
            lineItem.setProductEntity(product11);
            lineItem.setQuantity(1);
            newQtyToAdd = new BigDecimal(1);
            newSubtotal = product11.getUnitPrice().multiply(newQtyToAdd);
            lineItem.setSubTotal(newSubtotal);
            newBundle.getBundleLineItems().add(lineItem);
            
            newBundle.setSkuCode("BUND003");
            newBundle.setName("All-in-One Bodycare Set");
            newBundle.setDescription("New all-in-one body care set discounted for a limited time, selling out fast so get it now!");
            newBundle.setQuantityOnHand(20);
            newBundle.setReorderQuantity(50);
            newBundle.setUnitPrice(new BigDecimal("250"));
            newBundle.setBundleRating(5);

            BundleEntity newBundleEntity3 = bundleEntitySessionBeanLocal.createNewBundle(newBundle, tagIdsPopularDiscountNew);

        } catch (CreateNewCreditCardException | StaffUsernameExistException | UnknownPersistenceException | InputDataValidationException| ProductSkuCodeExistException | CreateNewCategoryException | CreateNewTagException | CreateNewProductException | CreateNewBrandException | BrandNotFoundException | CustomerUsernameExistException | CreateNewPersonalCartException | BundleSkuCodeExistException | CreateNewBundleException | ProductNotFoundException | CategoryNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
