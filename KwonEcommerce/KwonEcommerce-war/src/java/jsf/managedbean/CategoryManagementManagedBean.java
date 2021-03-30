package jsf.managedbean;

import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
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
import util.exception.CategoryNotFoundException;
import util.exception.CreateNewCategoryException;
import util.exception.DeleteCategoryException;
import util.exception.InputDataValidationException;

@Named
@ViewScoped

public class CategoryManagementManagedBean implements Serializable {

    @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    @EJB
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;

    @Inject
    private ViewProductManagedBean viewProductManagedBean;

    private List<ProductEntity> productEntities;
    private List<CategoryEntity> filteredCategoryEntities;

    private CategoryEntity newCategoryEntity;
    private Long parentCategoryId;
    private List<CategoryEntity> categoryEntities;
    private List<TagEntity> tagEntities;

    public CategoryManagementManagedBean() {
        newCategoryEntity = new CategoryEntity();
    }

    @PostConstruct
    public void postConstruct() {
        productEntities = productEntitySessionBeanLocal.retrieveAllProducts();
        categoryEntities = categoryEntitySessionBeanLocal.retrieveAllCategories();
        tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
    }

    public void viewProductDetails(ActionEvent event) throws IOException {
        Long productIdToView = (Long) event.getComponent().getAttributes().get("productId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }

    public void createNewCategory(ActionEvent event) {
        try {
            if (getParentCategoryId() == 0) {
                setParentCategoryId(null);
            }

            CategoryEntity ce = categoryEntitySessionBeanLocal.createNewCategoryEntity(newCategoryEntity, getParentCategoryId());
            categoryEntities.add(ce);

            if (filteredCategoryEntities != null) {
                filteredCategoryEntities.add(ce);
            }

            newCategoryEntity = new CategoryEntity();
            setParentCategoryId(null);

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New category created successfully (Category ID: " + ce.getCategoryId() + ")", null));
        } catch (InputDataValidationException | CreateNewCategoryException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new category: " + ex.getMessage(), null));
        }
    }

    public void deleteCategory(ActionEvent event) {
        try {
            CategoryEntity categoryEntityToDelete = (CategoryEntity) event.getComponent().getAttributes().get("categoryEntityToDelete");
            categoryEntitySessionBeanLocal.deleteCategory(categoryEntityToDelete.getCategoryId());

            categoryEntities.remove(categoryEntityToDelete);

            if (filteredCategoryEntities != null) {
                filteredCategoryEntities.remove(categoryEntityToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Category deleted successfully", null));
        } catch (CategoryNotFoundException | DeleteCategoryException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting category: " + ex.getMessage(), null));
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

    public List<CategoryEntity> getFilteredCategoryEntities() {
        return filteredCategoryEntities;
    }

    public void setFilteredCategoryEntities(List<CategoryEntity> filteredCategoryEntities) {
        this.filteredCategoryEntities = filteredCategoryEntities;
    }

    public CategoryEntity getNewCategoryEntity() {
        return newCategoryEntity;
    }

    public void setNewCategoryEntity(CategoryEntity newCategoryEntity) {
        this.newCategoryEntity = newCategoryEntity;
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

    public Long getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Long parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }
}
