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
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.exception.BrandNotFoundException;
import util.exception.CreateNewBrandException;
import util.exception.DeleteBrandException;
import util.exception.InputDataValidationException;

@Named
@ViewScoped

public class BrandManagementManagedBean implements Serializable {

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
    private List<BrandEntity> filteredBrandEntities;

    private BrandEntity newBrandEntity;
    private List<CategoryEntity> categoryEntities;
    private List<TagEntity> tagEntities;
    private List<BrandEntity> brandEntities;

    public BrandManagementManagedBean() {
        newBrandEntity = new BrandEntity();
    }

    @PostConstruct
    public void postConstruct() {
        productEntities = productEntitySessionBeanLocal.retrieveAllProducts();
        categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
        tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
        setBrandEntities(brandEntitySessionBeanLocal.retrieveAllBrands());
    }

    public void viewProductDetails(ActionEvent event) throws IOException {
        Long productIdToView = (Long) event.getComponent().getAttributes().get("productId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }

    public void createNewBrand(ActionEvent event) {
        try {
            BrandEntity be = brandEntitySessionBeanLocal.createNewBrandEntity(newBrandEntity);
            getBrandEntities().add(be);

            if (filteredBrandEntities != null) {
                filteredBrandEntities.add(be);
            }

            newBrandEntity = new BrandEntity();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New brand created successfully (Brand ID: " + be.getBrandId() + ")", null));
        } catch (InputDataValidationException | CreateNewBrandException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new brand: " + ex.getMessage(), null));
        }
    }

    public void deleteBrand(ActionEvent event) {
        try {
            BrandEntity brandEntityToDelete = (BrandEntity) event.getComponent().getAttributes().get("brandEntityToDelete");
            brandEntitySessionBeanLocal.deleteBrand(brandEntityToDelete.getBrandId());

            getBrandEntities().remove(brandEntityToDelete);

            if (filteredBrandEntities != null) {
                filteredBrandEntities.remove(brandEntityToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Brand deleted successfully", null));
        } catch (BrandNotFoundException | DeleteBrandException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting brand: " + ex.getMessage(), null));
        } catch (Exception ex) {
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

    public List<BrandEntity> getFilteredBrandEntities() {
        return filteredBrandEntities;
    }

    public void setFilteredBrandEntities(List<BrandEntity> filteredBrandEntities) {
        this.filteredBrandEntities = filteredBrandEntities;
    }

    public BrandEntity getNewBrandEntity() {
        return newBrandEntity;
    }

    public void setNewBrandEntity(BrandEntity newBrandEntity) {
        this.newBrandEntity = newBrandEntity;
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

    public List<BrandEntity> getBrandEntities() {
        return brandEntities;
    }

    public void setBrandEntities(List<BrandEntity> brandEntities) {
        this.brandEntities = brandEntities;
    }
}
