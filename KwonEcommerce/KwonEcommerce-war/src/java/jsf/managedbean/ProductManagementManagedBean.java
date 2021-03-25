/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.BrandEntitySessionBeanLocal;
import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.BrandEntity;
import entity.CategoryEntity;
import entity.ProductEntity;
import entity.TagEntity;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.exception.BrandNotFoundException;
import util.exception.CreateNewBrandException;
import util.exception.CreateNewProductException;
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
public class ProductManagementManagedBean implements Serializable {

    @EJB
    private BrandEntitySessionBeanLocal brandEntitySessionBeanLocal;

     @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    @EJB
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;
    
    
    @Inject
    private ViewProductManagedBean viewProductManagedBean;
    
    private List<ProductEntity> productEntities;
    private List<ProductEntity> filteredProductEntities;
    
    private ProductEntity newProductEntity;
    private Long categoryIdNew;
    private List<Long> tagIdsNew;
    private Long brandIdNew;
    private List<CategoryEntity> categoryEntities;
    private List<TagEntity> tagEntities;   
    private List<BrandEntity> brandEntities;
    
    private ProductEntity selectedProductEntityToUpdate;
    private Long categoryIdUpdate;
    private List<Long> tagIdsUpdate;
    private Long brandIdUpdate;
 
    
    
    
    public ProductManagementManagedBean()
    {
        newProductEntity = new ProductEntity();
    }
    
    
    
    @PostConstruct
    public void postConstruct()
    {
        productEntities = productEntitySessionBeanLocal.retrieveAllProducts();
        categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
        tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
        brandEntities = brandEntitySessionBeanLocal.retrieveAllBrands();
    }
    
    
    
    public void viewProductDetails(ActionEvent event) throws IOException
    {
        Long productIdToView = (Long)event.getComponent().getAttributes().get("productId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }
    
    
    
    public void createNewProduct(ActionEvent event)
    {        
        if(categoryIdNew == 0)
        {
            categoryIdNew = null;
        }                
        
        try
        {
            ProductEntity pe = productEntitySessionBeanLocal.createNewProduct(newProductEntity, categoryIdNew, tagIdsNew, brandIdNew);
            productEntities.add(pe);
            
            if(filteredProductEntities != null)
            {
                filteredProductEntities.add(pe);
            }
            
            newProductEntity = new ProductEntity();
            categoryIdNew = null;
            tagIdsNew = null;
            brandIdNew = null;
            

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New product created successfully (Product ID: " + pe.getProductId() + ")", null));
        }
        catch(InputDataValidationException | CreateNewProductException | ProductSkuCodeExistException | UnknownPersistenceException | CreateNewBrandException | BrandNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new product: " + ex.getMessage(), null));
        }
    }
    
    
    
    public void doUpdateProduct(ActionEvent event)
    {
        selectedProductEntityToUpdate = (ProductEntity)event.getComponent().getAttributes().get("productEntityToUpdate");
        
        categoryIdUpdate = selectedProductEntityToUpdate.getCategoryEntity().getCategoryId();
        setBrandIdUpdate(selectedProductEntityToUpdate.getBrandEntity().getBrandId());
        tagIdsUpdate = new ArrayList<>();

        for(TagEntity tagEntity:selectedProductEntityToUpdate.getTagEntities())
        {
            tagIdsUpdate.add(tagEntity.getTagId());
        }
    }
    
    
    
    public void updateProduct(ActionEvent event)
    {        
        if(categoryIdUpdate  == 0)
        {
            categoryIdUpdate = null;
        }                
        
        try
        {
            productEntitySessionBeanLocal.updateProduct(selectedProductEntityToUpdate, categoryIdUpdate, tagIdsUpdate, brandIdUpdate);
                        
            for(CategoryEntity ce:categoryEntities)
            {
                if(ce.getCategoryId().equals(categoryIdUpdate))
                {
                    selectedProductEntityToUpdate.setCategoryEntity(ce);
                    break;
                }                
            }
            
            selectedProductEntityToUpdate.getTagEntities().clear();
            
            for(TagEntity te:tagEntities)
            {
                if(tagIdsUpdate.contains(te.getTagId()))
                {
                    selectedProductEntityToUpdate.getTagEntities().add(te);
                }                
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product updated successfully", null));
        }
        catch(ProductNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while updating product: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }
    
    
    
    public void deleteProduct(ActionEvent event)
    {
        try
        {
            ProductEntity productEntityToDelete = (ProductEntity)event.getComponent().getAttributes().get("productEntityToDelete");
            productEntitySessionBeanLocal.deleteProduct(productEntityToDelete.getProductId());
            
            productEntities.remove(productEntityToDelete);
            
            if(filteredProductEntities != null)
            {
                filteredProductEntities.remove(productEntityToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product deleted successfully", null));
        }
        catch(ProductNotFoundException | DeleteProductException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting product: " + ex.getMessage(), null));
        }
        catch(Exception ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    
    
    public ViewProductManagedBean getViewProductManagedBean() {
        return viewProductManagedBean;
    }

    public void setViewProductManagedBean(ViewProductManagedBean viewProductManagedBean) {
        this.viewProductManagedBean = viewProductManagedBean;
    }
    
    public List<ProductEntity> getProductEntities() {
        return productEntities;
    }

    public void setProductEntities(List<ProductEntity> productEntities) {
        this.productEntities = productEntities;
    }

    public List<ProductEntity> getFilteredProductEntities() {
        return filteredProductEntities;
    }

    public void setFilteredProductEntities(List<ProductEntity> filteredProductEntities) {
        this.filteredProductEntities = filteredProductEntities;
    }

    public ProductEntity getNewProductEntity() {
        return newProductEntity;
    }

    public void setNewProductEntity(ProductEntity newProductEntity) {
        this.newProductEntity = newProductEntity;
    }
    
    public Long getCategoryIdNew() {
        return categoryIdNew;
    }

    public void setCategoryIdNew(Long categoryIdNew) {
        this.categoryIdNew = categoryIdNew;
    }

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
    
    public ProductEntity getSelectedProductEntityToUpdate() {
        return selectedProductEntityToUpdate;
    }

    public void setSelectedProductEntityToUpdate(ProductEntity selectedProductEntityToUpdate) {
        this.selectedProductEntityToUpdate = selectedProductEntityToUpdate;
    }

    public Long getCategoryIdUpdate() {
        return categoryIdUpdate;
    }

    public void setCategoryIdUpdate(Long categoryIdUpdate) {
        this.categoryIdUpdate = categoryIdUpdate;
    }

    public List<Long> getTagIdsUpdate() {
        return tagIdsUpdate;
    }

    public void setTagIdsUpdate(List<Long> tagIdsUpdate) {
        this.tagIdsUpdate = tagIdsUpdate;
    }   

    /**
     * @return the brandIdUpdate
     */
    public Long getBrandIdUpdate() {
        return brandIdUpdate;
    }

    /**
     * @param brandIdUpdate the brandIdUpdate to set
     */
    public void setBrandIdUpdate(Long brandIdUpdate) {
        this.brandIdUpdate = brandIdUpdate;
    }

    /**
     * @return the brandEntities
     */
    public List<BrandEntity> getBrandEntities() {
        return brandEntities;
    }

    /**
     * @param brandEntities the brandEntities to set
     */
    public void setBrandEntities(List<BrandEntity> brandEntities) {
        this.brandEntities = brandEntities;
    }
    
    
}
