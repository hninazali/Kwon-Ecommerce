/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.BrandEntitySessionBeanLocal;
import ejb.session.stateless.BundleEntitySessionBeanLocal;
import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import ejb.session.stateless.OrderTransactionSessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import entity.BrandEntity;
import entity.BundleEntity;
import entity.CategoryEntity;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import entity.ProductEntity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;

/**
 *
 * @author User
 */
@Named(value = "salesReportManagedBean")
@ViewScoped
public class SalesReportManagedBean implements Serializable{

    @EJB(name = "OrderTransactionSessionBeanLocal")
    private OrderTransactionSessionBeanLocal orderTransactionSessionBeanLocal;

    @EJB(name = "ProductEntitySessionBeanLocal")
    private ProductEntitySessionBeanLocal productEntitySessionBeanLocal;

    @EJB(name = "CategoryEntitySessionBeanLocal")
    private CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal;

    @EJB(name = "BrandEntitySessionBeanLocal")
    private BrandEntitySessionBeanLocal brandEntitySessionBeanLocal;

    @EJB(name = "BundleEntitySessionBeanLocal")
    private BundleEntitySessionBeanLocal bundleEntitySessionBeanLocal;
    
    private List<OrderLineItemEntity> lineItems;
    private List<OrderTransactionEntity> orderTransactions;
    private List<CategoryEntity> categories;
    private List<BrandEntity> brands;
    private List<BundleEntity> bundles;
    private List<ProductEntity> products;
    
    private Date startDate;
    private Date endDate;
    
    private CategoryEntity chosenCategory;
    private BrandEntity chosenBrand;
    private BundleEntity chosenBundle;
    private ProductEntity chosenProduct;
    
    

    /**
     * Creates a new instance of SalesReportManagedBean
     */
    public SalesReportManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct()
    {
        orderTransactions = orderTransactionSessionBeanLocal.retrieveAllOrderTransactions();
        categories = categoryEntitySessionBeanLocal.retrieveAllCategories();
        brands = brandEntitySessionBeanLocal.retrieveAllBrands();
        bundles = bundleEntitySessionBeanLocal.retrieveAllBundles();
        products = productEntitySessionBeanLocal.retrieveAllProducts();
    }
    
    public void filterDate(Date startDate, Date endDate, List<OrderTransactionEntity> orders)
    {
        
    }
    
    public void showReport(ActionEvent event)
    {
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        
        startDate = null;
        endDate = null;
        orderTransactions = new ArrayList<>();
    }
    
    public void showReportWithDate(ActionEvent event)
    {
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (startDate != null)
        {
            filterDate(startDate, endDate, orderTransactions);
        }
        
        startDate = null;
        endDate = null;
        orderTransactions = new ArrayList<>();
    }
    
    public void showReportByCategory(ActionEvent event)
    {
        filterCategory(chosenCategory, orderTransactions);
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (startDate != null)
        {
            filterDate(startDate, endDate, orderTransactions);
        }
        
        startDate = null;
        endDate = null;
        orderTransactions = new ArrayList<>();
    }
    
    public void showReportByBrand(ActionEvent event)
    {
        filterBrand(chosenBrand, orderTransactions);
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (startDate != null)
        {
            filterDate(startDate, endDate, orderTransactions);
        }
        
        startDate = null;
        endDate = null;
        orderTransactions = new ArrayList<>();
    }
    
    public void showReportByBundle(ActionEvent event)
    {
        filterBundle(chosenBundle, orderTransactions);
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (startDate != null)
        {
            filterDate(startDate, endDate, orderTransactions);
        }
        
        startDate = null;
        endDate = null;
        orderTransactions = new ArrayList<>();
    }
    
    public void showReportByProduct(ActionEvent event)
    {
        filterProduct(chosenProduct, orderTransactions);
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (startDate != null)
        {
            filterDate(startDate, endDate, orderTransactions);
        }
        
        startDate = null;
        endDate = null;
        orderTransactions = new ArrayList<>();
    }
    
    public void filterCategory(CategoryEntity category, List<OrderTransactionEntity> orders)
    {
        
    }
    
    public void filterBundle(BundleEntity bundle, List<OrderTransactionEntity> orders)
    {
        
    }
    
    public void filterBrand(BrandEntity brand, List<OrderTransactionEntity> orders)
    {
        
    }
    
    public void filterProduct(ProductEntity product, List<OrderTransactionEntity> orders)
    {
        
    }
    
}
