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
import util.exception.CreateNewTagException;
import util.exception.DeleteTagException;
import util.exception.InputDataValidationException;
import util.exception.TagNotFoundException;

@Named
@ViewScoped

public class TagManagementManagedBean implements Serializable {

    @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;
    @EJB
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;

    @Inject
    private ViewProductManagedBean viewProductManagedBean;

    private List<ProductEntity> productEntities;
    private List<TagEntity> filteredTagEntities;

    private TagEntity newTagEntity;
    private List<CategoryEntity> categoryEntities;
    private List<TagEntity> tagEntities;

    public TagManagementManagedBean() {
        newTagEntity = new TagEntity();
    }

    @PostConstruct
    public void postConstruct() {
        productEntities = productEntitySessionBeanLocal.retrieveAllProducts();
        categoryEntities = categoryEntitySessionBeanLocal.retrieveAllLeafCategories();
        tagEntities = tagEntitySessionBeanLocal.retrieveAllTags();
    }

    public void viewProductDetails(ActionEvent event) throws IOException {
        Long productIdToView = (Long) event.getComponent().getAttributes().get("productId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToView);
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }

    public void createNewTag(ActionEvent event) {
        try {
            TagEntity te = tagEntitySessionBeanLocal.createNewTagEntity(newTagEntity);
            tagEntities.add(te);

            if (filteredTagEntities != null) {
                filteredTagEntities.add(te);
            }

            newTagEntity = new TagEntity();

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "New tag created successfully (Tag ID: " + te.getTagId() + ")", null));
        } catch (InputDataValidationException | CreateNewTagException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while creating the new tag: " + ex.getMessage(), null));
        }
    }

    public void deleteTag(ActionEvent event) {
        try {
            TagEntity tagEntityToDelete = (TagEntity) event.getComponent().getAttributes().get("tagEntityToDelete");
            tagEntitySessionBeanLocal.deleteTag(tagEntityToDelete.getTagId());

            tagEntities.remove(tagEntityToDelete);

            if (filteredTagEntities != null) {
                filteredTagEntities.remove(tagEntityToDelete);
            }

            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Tag deleted successfully", null));
        } catch (TagNotFoundException | DeleteTagException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error has occurred while deleting tag: " + ex.getMessage(), null));
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

    public List<TagEntity> getFilteredTagEntities() {
        return filteredTagEntities;
    }

    public void setFilteredTagEntities(List<TagEntity> filteredTagEntities) {
        this.filteredTagEntities = filteredTagEntities;
    }

    public TagEntity getNewTagEntity() {
        return newTagEntity;
    }

    public void setNewTagEntity(TagEntity newTagEntity) {
        this.newTagEntity = newTagEntity;
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
}
