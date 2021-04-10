/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.BundleEntitySessionBeanLocal;
import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.BundleEntity;
import entity.BundleLineItemEntity;
import entity.ProductEntity;
import entity.TagEntity;
import java.io.Serializable;
import java.math.BigDecimal;
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
import util.exception.BundleSkuCodeExistException;
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewBundleException;
import util.exception.InputDataValidationException;
import util.exception.ProductNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author User
 */
@Named(value = "createNewBundleManagedBean")
@ViewScoped
public class CreateNewBundleManagedBean implements Serializable {

    @EJB(name = "CategoryEntitySessionBeanLocal")
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;

    @EJB(name = "TagEntitySessionBeanLocal")
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;

    @EJB(name = "ProductEntitySessionBeanLocal")
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;

    @EJB(name = "BundleEntitySessionBeanLocal")
    private BundleEntitySessionBeanLocal bundleEntitySessionBeanLocal;

    private BundleEntity newBundle;
    private List<BundleLineItemEntity> lineItems;
    private List<ProductEntity> products;
    private List<ProductEntity> filteredProductEntities;
    private List<TagEntity> tags;
    private ProductEntity selectedProductEntityToAdd;
    private Integer quantityToAdd;
    private List<Long> tagIdsNew;

    /**
     * Creates a new instance of CreateNewBundleManagedBean
     */
    public CreateNewBundleManagedBean() {
        lineItems = new ArrayList<>();
        newBundle = new BundleEntity();
        selectedProductEntityToAdd = new ProductEntity();
    }

    @PostConstruct
    public void postConstruct() {
        setProducts(getProductEntitySessionBeanLocal().retrieveAllProducts());
        //categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
        setTags(getTagEntitySessionBeanLocal().retrieveAllTags());
        //setBrandEntities(brandEntitySessionBeanLocal.retrieveAllBrands());
    }

    public void doAddLineItem(ActionEvent event) {
        try {
            Long productId = (Long) event.getComponent().getAttributes().get("productId");
            selectedProductEntityToAdd = productEntitySessionBeanLocal.retrieveProductByProductId(productId);
        } catch (ProductNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new bundle: " + ex.getMessage(), null));
        }
    }

    public void addLineItem(ActionEvent event) {
        BundleLineItemEntity lineItem = new BundleLineItemEntity();
        lineItem.setProductEntity(selectedProductEntityToAdd);
        lineItem.setQuantity(quantityToAdd);
        BigDecimal newQtyToAdd = new BigDecimal(quantityToAdd);
        BigDecimal newSubtotal = selectedProductEntityToAdd.getUnitPrice().multiply(newQtyToAdd);
        lineItem.setSubTotal(newSubtotal);
        lineItems.add(lineItem);

        selectedProductEntityToAdd = new ProductEntity();
        quantityToAdd = 0;

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added!!!", null));
    }

    public void createBundle(ActionEvent event) {
        try {
            BundleEntity newBundleEntity = bundleEntitySessionBeanLocal.createNewBundle(newBundle, tagIdsNew);
        } catch (BundleSkuCodeExistException | UnknownPersistenceException | InputDataValidationException | CreateNewBundleException | ProductNotFoundException | CategoryNotFoundException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new bundle: " + ex.getMessage(), null));
        }
    }

    public CategoryEntitySessionBeanLocal getCategoryEntitySessionBeanLocal() {
        return categoryEntitySessionBeanLocal;
    }

    public void setCategoryEntitySessionBeanLocal(CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal) {
        this.categoryEntitySessionBeanLocal = categoryEntitySessionBeanLocal;
    }

    public TagEntitySessionBeanLocal getTagEntitySessionBeanLocal() {
        return tagEntitySessionBeanLocal;
    }

    public void setTagEntitySessionBeanLocal(TagEntitySessionBeanLocal tagEntitySessionBeanLocal) {
        this.tagEntitySessionBeanLocal = tagEntitySessionBeanLocal;
    }

    public ProductEntitySessionBeanLocal getProductEntitySessionBeanLocal() {
        return productEntitySessionBeanLocal;
    }

    public void setProductEntitySessionBeanLocal(ProductEntitySessionBeanLocal productEntitySessionBeanLocal) {
        this.productEntitySessionBeanLocal = productEntitySessionBeanLocal;
    }

    public BundleEntitySessionBeanLocal getBundleEntitySessionBeanLocal() {
        return bundleEntitySessionBeanLocal;
    }

    public void setBundleEntitySessionBeanLocal(BundleEntitySessionBeanLocal bundleEntitySessionBeanLocal) {
        this.bundleEntitySessionBeanLocal = bundleEntitySessionBeanLocal;
    }

    public BundleEntity getNewBundle() {
        return newBundle;
    }

    public void setNewBundle(BundleEntity newBundle) {
        this.newBundle = newBundle;
    }

    public List<BundleLineItemEntity> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<BundleLineItemEntity> lineItems) {
        this.lineItems = lineItems;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    public List<TagEntity> getTags() {
        return tags;
    }

    public void setTags(List<TagEntity> tags) {
        this.tags = tags;
    }

    public ProductEntity getSelectedProductEntityToAdd() {
        return selectedProductEntityToAdd;
    }

    public void setSelectedProductEntityToAdd(ProductEntity selectedProductEntityToAdd) {
        this.selectedProductEntityToAdd = selectedProductEntityToAdd;
    }

    public Integer getQuantityToAdd() {
        return quantityToAdd;
    }

    public void setQuantityToAdd(Integer quantityToAdd) {
        this.quantityToAdd = quantityToAdd;
    }

    public List<Long> getTagIdsNew() {
        return tagIdsNew;
    }

    public void setTagIdsNew(List<Long> tagIdsNew) {
        this.tagIdsNew = tagIdsNew;
    }

    public List<ProductEntity> getFilteredProductEntities() {
        return filteredProductEntities;
    }

    public void setFilteredProductEntities(List<ProductEntity> filteredProductEntities) {
        this.filteredProductEntities = filteredProductEntities;
    }

}
