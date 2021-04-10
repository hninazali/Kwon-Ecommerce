/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.BrandEntitySessionBeanLocal;
import ejb.session.stateless.BundleEntitySessionBeanLocal;
import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.BrandEntity;
import entity.BundleEntity;
import entity.CategoryEntity;
import entity.ProductEntity;
import entity.TagEntity;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.exception.BrandNotFoundException;
import util.exception.BundleNotFoundException;
import util.exception.BundleSkuCodeExistException;
import util.exception.CreateNewBrandException;
import util.exception.CreateNewBundleException;
import util.exception.CreateNewProductException;
import util.exception.DeleteBundleException;
import util.exception.DeleteProductException;
import util.exception.InputDataValidationException;
import util.exception.ProductNotFoundException;
import util.exception.ProductSkuCodeExistException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author hninazali
 */
@Named
@ViewScoped
public class BundleManagementManagedBean implements Serializable {

    @EJB
    private BundleEntitySessionBeanLocal bundleEntitySessionBeanLocal;

//    @EJB
//    private BrandEntitySessionBeanLocal brandEntitySessionBeanLocal;

     @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    @EJB
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;
    
    
    
    @Inject
//    private ViewProductManagedBean viewProductManagedBean;
    private ViewBundleManagedBean viewBundleManagedBean;
    
    //private List<ProductEntity> productEntities;
//    private List<ProductEntity> filteredProductEntities;
    
    private List<BundleEntity> bundleEntities;
    private List<BundleEntity> filteredBundleEntities;
    
 //   private ProductEntity newProductEntity;
    private BundleEntity newBundleEntity;
    
//    private Long categoryIdNew;
    private List<Long> tagIdsNew;
//    private Long brandIdNew;
    private List<Long> productIdsNew;
    
    private List<CategoryEntity> categoryEntities;
    private List<TagEntity> tagEntities;   
    private List<BrandEntity> brandEntities;
//    
    
    private BundleEntity selectedBundleEntityToUpdate;
//    private Long categoryIdUpdate;
    private List<Long> tagIdsUpdate;
    private List<Long> productIdsUpdate;
    
//    private Long brandIdUpdate;
 
    
    private ProductEntity productToOrder;
    private Integer quantityToOrder = 0;
    
    
    
    public BundleManagementManagedBean()
    {
        newBundleEntity = new BundleEntity();
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
        setBundleEntities(getBundleEntitySessionBeanLocal().retrieveAllBundles());
        categoryEntities = getCategoryEntitySessionBeanLocal().retrieveAllLeafCategories();
        tagEntities = getTagEntitySessionBeanLocal().retrieveAllTags();
//      setBrandEntities(brandEntitySessionBeanLocal.retrieveAllBrands());
    }
    
    
    
    public void viewBundleDetails(ActionEvent event) throws IOException
    {
        Long bundleIdToView = (Long)event.getComponent().getAttributes().get("bundleId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("bundleIdToView", bundleIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewBundleDetails.xhtml");
    }
    
    
    
    public void createNewBundle(ActionEvent event)
    {        
//        if(categoryIdNew == 0)
//        {
//            categoryIdNew = null;
//        }                
        
        try
        {
            BundleEntity be = getBundleEntitySessionBeanLocal().createNewBundle(getNewBundleEntity(), tagIdsNew);
//          ProductEntity pe = productEntitySessionBeanLocal.createNewProduct(newProductEntity, categoryIdNew, tagIdsNew, brandIdNew);
            
            if(getFilteredBundleEntities() != null)
            {
                getFilteredBundleEntities().add(be);
            }
            
            setNewBundleEntity(new BundleEntity());
//            categoryIdNew = null;
            tagIdsNew = null;
//            brandIdNew = null;
            

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New bundle created successfully (Bundle ID: " + be.getBundleId() + ")", null));
        }
        catch(InputDataValidationException | CreateNewBundleException | BundleSkuCodeExistException | UnknownPersistenceException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new bundle: " + ex.getMessage(), null));
        }
    }
    
    
    
    public void doUpdateBundle(ActionEvent event)
    {
        setSelectedBundleEntityToUpdate((BundleEntity)event.getComponent().getAttributes().get("bundleEntityToUpdate"));
        
//        categoryIdUpdate = selectedBundleEntityToUpdate.getCategoryEntity().getCategoryId();
//        setBrandIdUpdate(selectedBundleEntityToUpdate.getBrandEntity().getBrandId());
        tagIdsUpdate = new ArrayList<>();

        for(TagEntity tagEntity:getSelectedBundleEntityToUpdate().getTagEntities())
        {
            tagIdsUpdate.add(tagEntity.getTagId());
        }
    }
    
    
    
    public void updateBundle(ActionEvent event)
    {        
//        if(categoryIdUpdate  == 0)
//        {
//            categoryIdUpdate = null;
//        }                
        
        try
        {
            getBundleEntitySessionBeanLocal().updateBundle(getSelectedBundleEntityToUpdate());
                        
//            for(CategoryEntity ce:categoryEntities)
//            {
//                if(ce.getCategoryId().equals(categoryIdUpdate))
//                {
//                    selectedProductEntityToUpdate.setCategoryEntity(ce);
//                    break;
//                }                
//            }
            
//            getSelectedBundleEntityToUpdate().getTagEntities().clear();
//            
//            for(TagEntity te:tagEntities)
//            {
//                if(tagIdsUpdate.contains(te.getTagId()))
//                {
//                    getSelectedBundleEntityToUpdate().getTagEntities().add(te);
//                }                
//            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Bundle updated successfully", null));
        }
        catch(BundleNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating bundle: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    
    
    public void deleteBundle(ActionEvent event)
    {
        try
        {
            BundleEntity bundleEntityToDelete = (BundleEntity)event.getComponent().getAttributes().get("bundleEntityToDelete");
            getBundleEntitySessionBeanLocal().deleteBundle(bundleEntityToDelete.getBundleId());
            
            getBundleEntities().remove(bundleEntityToDelete);
            
            if(getFilteredBundleEntities() != null)
            {
                getFilteredBundleEntities().remove(bundleEntityToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Bundle deleted successfully", null));
        }
        catch(BundleNotFoundException | DeleteBundleException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting bundle: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
//    public void doOrderProduct(ActionEvent event)
//    {
//        productToOrder = (ProductEntity)event.getComponent().getAttributes().get("productEntityToOrder");
//    }
//    
//    public void orderProduct(ActionEvent event) {
//         try {
//             productEntitySessionBeanLocal.orderProduct(productToOrder.getProductId(), quantityToOrder);
//         } catch (ProductNotFoundException ex) {
//             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
//         }
//        
//    }
    
//    public ViewProductManagedBean getViewProductManagedBean() {
//        return viewProductManagedBean;
//    }
//
//    public void setViewProductManagedBean(ViewProductManagedBean viewProductManagedBean) {
//        this.viewProductManagedBean = viewProductManagedBean;
//    }
//    
//    public List<ProductEntity> getProductEntities() {
//        return productEntities;
//    }
//
//    public void setProductEntities(List<ProductEntity> productEntities) {
//        this.productEntities = productEntities;
//    }
//
//    public List<ProductEntity> getFilteredProductEntities() {
//        return filteredProductEntities;
//    }
//
//    public void setFilteredProductEntities(List<ProductEntity> filteredProductEntities) {
//        this.filteredProductEntities = filteredProductEntities;
//    }

//    public ProductEntity getNewProductEntity() {
//        return newProductEntity;
//    }
//
//    public void setNewProductEntity(ProductEntity newProductEntity) {
//        this.newProductEntity = newProductEntity;
//    }
//    
//    public Long getCategoryIdNew() {
//        return categoryIdNew;
//    }
//
//    public void setCategoryIdNew(Long categoryIdNew) {
//        this.categoryIdNew = categoryIdNew;
//    }

    public List<Long> getTagIdsNew() {
        return tagIdsNew;
    }

    public void setTagIdsNew(List<Long> tagIdsNew) {
        this.tagIdsNew = tagIdsNew;
    }

    public List<CategoryEntity> getCategoryEntities() {
        return categoryEntities;
    }

    public void setCategoryEntities(List<CategoryEntity> categoryEntities) {
        this.categoryEntities = categoryEntities;
    }

    public List<TagEntity> getTagEntities() {
        return tagEntities;
    }

    public void setTagEntities(List<TagEntity> tagEntities) {
        this.tagEntities = tagEntities;
    }
    
//    public ProductEntity getSelectedProductEntityToUpdate() {
//        return selectedProductEntityToUpdate;
//    }
//
//    public void setSelectedProductEntityToUpdate(ProductEntity selectedProductEntityToUpdate) {
//        this.selectedProductEntityToUpdate = selectedProductEntityToUpdate;
//    }
//
//    public Long getCategoryIdUpdate() {
//        return categoryIdUpdate;
//    }
//
//    public void setCategoryIdUpdate(Long categoryIdUpdate) {
//        this.categoryIdUpdate = categoryIdUpdate;
//    }

    public List<Long> getTagIdsUpdate() {
        return tagIdsUpdate;
    }

    public void setTagIdsUpdate(List<Long> tagIdsUpdate) {
        this.tagIdsUpdate = tagIdsUpdate;
    }   

    public ProductEntity getProductToOrder() {
        return productToOrder;
    }

    public void setProductToOrder(ProductEntity productToOrder) {
        this.productToOrder = productToOrder;
    }

    public Integer getQuantityToOrder() {
        return quantityToOrder;
    }

    public void setQuantityToOrder(Integer quantityToOrder) {
        this.quantityToOrder = quantityToOrder;
    }

//    public Long getBrandIdUpdate() {
//        return brandIdUpdate;
//    }
//
//    public void setBrandIdUpdate(Long brandIdUpdate) {
//        this.brandIdUpdate = brandIdUpdate;
//    }

    public List<BrandEntity> getBrandEntities() {
        return brandEntities;
    }

    public void setBrandEntities(List<BrandEntity> brandEntities) {
        this.brandEntities = brandEntities;
    }

    /**
     * @return the viewBundleManagedBean
     */
    public ViewBundleManagedBean getViewBundleManagedBean() {
        return viewBundleManagedBean;
    }

    /**
     * @param viewBundleManagedBean the viewBundleManagedBean to set
     */
    public void setViewBundleManagedBean(ViewBundleManagedBean viewBundleManagedBean) {
        this.viewBundleManagedBean = viewBundleManagedBean;
    }

    /**
     * @return the productIdsNew
     */
    public List<Long> getProductIdsNew() {
        return productIdsNew;
    }

    /**
     * @param productIdsNew the productIdsNew to set
     */
    public void setProductIdsNew(List<Long> productIdsNew) {
        this.productIdsNew = productIdsNew;
    }

    /**
     * @return the bundleEntities
     */
    public List<BundleEntity> getBundleEntities() {
        return bundleEntities;
    }

    /**
     * @param bundleEntities the bundleEntities to set
     */
    public void setBundleEntities(List<BundleEntity> bundleEntities) {
        this.bundleEntities = bundleEntities;
    }
    /**
     * @return the newBundleEntity
     */
    public BundleEntity getNewBundleEntity() {
        return newBundleEntity;
    }

    /**
     * @param newBundleEntity the newBundleEntity to set
     */
    public void setNewBundleEntity(BundleEntity newBundleEntity) {
        this.newBundleEntity = newBundleEntity;
    }

    /**
     * @return the bundleEntitySessionBeanLocal
     */
    public BundleEntitySessionBeanLocal getBundleEntitySessionBeanLocal() {
        return bundleEntitySessionBeanLocal;
    }

    /**
     * @param bundleEntitySessionBeanLocal the bundleEntitySessionBeanLocal to set
     */
    public void setBundleEntitySessionBeanLocal(BundleEntitySessionBeanLocal bundleEntitySessionBeanLocal) {
        this.bundleEntitySessionBeanLocal = bundleEntitySessionBeanLocal;
    }
    
    /**
     * @return the productEntitySessionBeanLocal
     */
    public ProductEntitySessionBeanLocal getProductEntitySessionBeanLocal() {
        return productEntitySessionBeanLocal;
    }
    
    /**
     * @param productEntitySessionBeanLocal the productEntitySessionBeanLocal to set
     */
    public void setProductEntitySessionBeanLocal(ProductEntitySessionBeanLocal productEntitySessionBeanLocal) {
        this.productEntitySessionBeanLocal = productEntitySessionBeanLocal;
    }

    /**
     * @return the categoryEntitySessionBeanLocal
     */
    public CategoryEntitySessionBeanLocal getCategoryEntitySessionBeanLocal() {
        return categoryEntitySessionBeanLocal;
    }

    /**
     * @param categoryEntitySessionBeanLocal the categoryEntitySessionBeanLocal to set
     */
    public void setCategoryEntitySessionBeanLocal(CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal) {
        this.categoryEntitySessionBeanLocal = categoryEntitySessionBeanLocal;
    }

    /**
     * @return the tagEntitySessionBeanLocal
     */
    public TagEntitySessionBeanLocal getTagEntitySessionBeanLocal() {
        return tagEntitySessionBeanLocal;
    }

    /**
     * @param tagEntitySessionBeanLocal the tagEntitySessionBeanLocal to set
     */
    public void setTagEntitySessionBeanLocal(TagEntitySessionBeanLocal tagEntitySessionBeanLocal) {
        this.tagEntitySessionBeanLocal = tagEntitySessionBeanLocal;
    }

    /**
     * @return the filteredBundleEntities
     */
    public List<BundleEntity> getFilteredBundleEntities() {
        return filteredBundleEntities;
    }

    /**
     * @param filteredBundleEntities the filteredBundleEntities to set
     */
    public void setFilteredBundleEntities(List<BundleEntity> filteredBundleEntities) {
        this.filteredBundleEntities = filteredBundleEntities;
    }

    /**
     * @return the selectedBundleEntityToUpdate
     */
    public BundleEntity getSelectedBundleEntityToUpdate() {
        return selectedBundleEntityToUpdate;
    }

    /**
     * @param selectedBundleEntityToUpdate the selectedBundleEntityToUpdate to set
     */
    public void setSelectedBundleEntityToUpdate(BundleEntity selectedBundleEntityToUpdate) {
        this.selectedBundleEntityToUpdate = selectedBundleEntityToUpdate;
    }
    
    public List<Long> getProductIdsUpdate() {
        return productIdsUpdate;
    }
    
    /**
     * @param productIdsUpdate the productIdsUpdate to set
     */
    public void setProductIdsUpdate(List<Long> productIdsUpdate) {
        this.productIdsUpdate = productIdsUpdate;
    }
    
    
}
