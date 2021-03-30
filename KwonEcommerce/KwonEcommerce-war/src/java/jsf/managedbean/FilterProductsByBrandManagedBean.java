package jsf.managedbean;

import ejb.session.stateless.BrandEntitySessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.BrandEntity;
import entity.ProductEntity;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import util.exception.BrandNotFoundException;

@Named(value = "filterProductsByBrandManagedBean")
@ViewScoped

public class FilterProductsByBrandManagedBean implements Serializable {

    @EJB
    private BrandEntitySessionBeanLocal brandEntitySessionBeanLocal;
    @EJB
    private TagEntitySessionBeanLocal tagEntitySessionBeanLocal;
    @EJB
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;

    @Inject
    private ViewProductManagedBean viewProductManagedBean;

    private Long selectedBrandId;
    private List<SelectItem> selectItems;
    private List<ProductEntity> productEntities;

    public FilterProductsByBrandManagedBean() {
    }

    @PostConstruct
    public void postConstruct() {
        List<BrandEntity> brandEntities = brandEntitySessionBeanLocal.retrieveAllBrands();
        selectItems = new ArrayList<>();

        for (BrandEntity brandEntity : brandEntities) {
            selectItems.add(new SelectItem(brandEntity.getBrandId(), brandEntity.getName(), brandEntity.getName()));
        }

        selectedBrandId = (Long) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("productFilterBrand");

        filterProduct();
    }

    @PreDestroy
    public void preDestroy() {
        // FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("TagEntityConverter_tagEntities", null);
        // FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove("TagEntityConverter_tagEntities", null);
    }

    public void filterProduct() {
        if (selectedBrandId != null) {
            try {
                productEntities = productEntitySessionBeanLocal.filterProductsByBrand(selectedBrandId);
            } catch (BrandNotFoundException ex) {
                productEntities = productEntitySessionBeanLocal.retrieveAllProducts();
            }
        } else {
            productEntities = productEntitySessionBeanLocal.retrieveAllProducts();
        }
    }

    public void viewProductDetails(ActionEvent event) throws IOException {
        Long productIdToView = (Long) event.getComponent().getAttributes().get("productId");
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("productIdToView", productIdToView);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("backMode", "filterProductsByBrand");
        FacesContext.getCurrentInstance().getExternalContext().redirect("viewProductDetails.xhtml");
    }

    public ViewProductManagedBean getViewProductManagedBean() {
        return viewProductManagedBean;
    }

    public void setViewProductManagedBean(ViewProductManagedBean viewProductManagedBean) {
        this.viewProductManagedBean = viewProductManagedBean;
    }

    public Long getSelectedBrandId() {
        return selectedBrandId;
    }

    public void setSelectedBrandId(Long selectedbrandId) {
        this.selectedBrandId = selectedbrandId;

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("productFilterBrand", selectedBrandId);
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(List<SelectItem> selectItems) {
        this.selectItems = selectItems;
    }

    public List<ProductEntity> getProductEntities() {
        return productEntities;
    }

    public void setProductEntities(List<ProductEntity> productEntities) {
        this.productEntities = productEntities;
    }
}
