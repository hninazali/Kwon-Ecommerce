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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import org.primefaces.event.FileUploadEvent;
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
    private String newName;
    
    private List<CategoryEntity> categoryEntities;
    private List<TagEntity> tagEntities;   
    private List<BrandEntity> brandEntities;
    
    private ProductEntity selectedProductEntityToUpdate;
    private Long categoryIdUpdate;
    private List<Long> tagIdsUpdate;
    private Long brandIdUpdate;
 
    
    private ProductEntity productToOrder;
    private Integer quantityToOrder = 0;
    
    
    
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
        setBrandEntities(brandEntitySessionBeanLocal.retrieveAllBrands());
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
        
        if(getBrandIdNew() == 0)
        {
            setBrandIdNew(null);
        }
        
        try
        {
            ProductEntity pe = productEntitySessionBeanLocal.createNewProduct(newProductEntity, categoryIdNew, tagIdsNew, getBrandIdNew());
            productEntities.add(pe);
            
            if(filteredProductEntities != null)
            {
                filteredProductEntities.add(pe);
            }
            
            newProductEntity = new ProductEntity();
            categoryIdNew = null;
            tagIdsNew = null;
            setBrandIdNew(null);
            

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New product created successfully (Product ID: " + pe.getProductId() + ")", null));
        }
        catch(InputDataValidationException | CreateNewProductException | ProductSkuCodeExistException | UnknownPersistenceException | CreateNewBrandException | BrandNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new product: " + ex.getMessage(), null));
        }
    }
    
    public void createNewProductTemp()
    {        
        if(categoryIdNew == 0)
        {
            categoryIdNew = null;
        }    
        
        if(getBrandIdNew() == 0)
        {
            setBrandIdNew(null);
        }
        
        try
        {
            ProductEntity pe = productEntitySessionBeanLocal.createNewProduct(newProductEntity, categoryIdNew, tagIdsNew, getBrandIdNew());
            productEntities.add(pe);
            
            if(filteredProductEntities != null)
            {
                filteredProductEntities.add(pe);
            }
            
            newProductEntity = new ProductEntity();
            categoryIdNew = null;
            tagIdsNew = null;
            setBrandIdNew(null);
            

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New product created successfully (Product ID: " + pe.getProductId() + ")", null));
        }
        catch(InputDataValidationException | CreateNewProductException | ProductSkuCodeExistException | UnknownPersistenceException | CreateNewBrandException | BrandNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new product: " + ex.getMessage(), null));
        }
    }
    
    public void doUpload(ActionEvent event)
    {
        newName = (String) event.getComponent().getAttributes().get("productName");
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
            productEntitySessionBeanLocal.updateProduct(selectedProductEntityToUpdate, categoryIdUpdate, tagIdsUpdate, getBrandIdUpdate());
                        
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
    
    public void doOrderProduct(ActionEvent event)
    {
        productToOrder = (ProductEntity)event.getComponent().getAttributes().get("productEntityToOrder");
    }
    
    public void orderProduct(ActionEvent event) {
         try {
             productEntitySessionBeanLocal.orderProduct(productToOrder.getProductId(), quantityToOrder);
         } catch (ProductNotFoundException ex) {
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
         }
        
    }
    
    public void handleFileUpload(FileUploadEvent event)
    {
        try
        {
            String newFilePath = FacesContext.getCurrentInstance().getExternalContext().getInitParameter("alternatedocroot_1") + System.getProperty("file.separator") + this.newName + ".jpg";

            System.err.println("********** Demo03ManagedBean.handleFileUpload(): File name: " + event.getFile().getFileName());
            System.err.println("********** Demo03ManagedBean.handleFileUpload(): newFilePath: " + newFilePath);

            File file = new File(newFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            int a;
            int BUFFER_SIZE = 8192;
            byte[] buffer = new byte[BUFFER_SIZE];

            InputStream inputStream = event.getFile().getInputStream();

            while (true)
            {
                a = inputStream.read(buffer);

                if (a < 0)
                {
                    break;
                }

                fileOutputStream.write(buffer, 0, a);
                fileOutputStream.flush();
            }

            fileOutputStream.close();
            inputStream.close();
            
            this.createNewProductTemp();
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,  "File uploaded successfully", ""));
        }
        catch(IOException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,  "File upload error: " + ex.getMessage(), ""));
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

    public Long getBrandIdUpdate() {
        return brandIdUpdate;
    }

    public void setBrandIdUpdate(Long brandIdUpdate) {
        this.brandIdUpdate = brandIdUpdate;
    }

    public List<BrandEntity> getBrandEntities() {
        return brandEntities;
    }

    public void setBrandEntities(List<BrandEntity> brandEntities) {
        this.brandEntities = brandEntities;
    }

    /**
     * @return the brandIdNew
     */
    public Long getBrandIdNew() {
        return brandIdNew;
    }

    /**
     * @param brandIdNew the brandIdNew to set
     */
    public void setBrandIdNew(Long brandIdNew) {
        this.brandIdNew = brandIdNew;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
    
    
}
