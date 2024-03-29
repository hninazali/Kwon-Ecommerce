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
import entity.BundleLineItemEntity;
import entity.CategoryEntity;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import entity.ProductEntity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javafx.animation.Animation;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;
import org.primefaces.model.charts.pie.PieChartDataSet;
import org.primefaces.model.charts.pie.PieChartModel;

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
    
    private List<OrderLineItemEntity> filteredByBrand;
    
    //private PieChartModel pieModel;
    private BarChartModel barModelBrand;
    private BarChartModel barModelCategory;
    private BarChartModel barModelProduct;
    private BarChartModel barModelMonthly;
    
    

    /**
     * Creates a new instance of SalesReportManagedBean
     */
    public SalesReportManagedBean() {
        lineItems = new ArrayList<>();
        orderTransactions = new ArrayList<>();
        categories = new ArrayList<>();
        brands = new ArrayList<>();
        products = new ArrayList<>();
    }
    
    @PostConstruct
    public void postConstruct()
    {
        orderTransactions = orderTransactionSessionBeanLocal.retrieveAllOrderTransactions();
        categories = categoryEntitySessionBeanLocal.retrieveAllCategories();
        brands = brandEntitySessionBeanLocal.retrieveAllBrands();
        products = productEntitySessionBeanLocal.retrieveAllProducts();
        
        createBrandBarModel();
        createCategoryBarModel();
        createMonthlyBarModel();
        createProductBarModel();
        System.out.println("Is this reachable????");
    }
    
    public void filterDate(Date startDate, Date endDate, List<OrderTransactionEntity> orders)
    {
        
    }
    
    public void showReport(ActionEvent event)
    {
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        
        setStartDate(null);
        setEndDate(null);
        setOrderTransactions(new ArrayList<>());
    }
    
    public void showReportWithDate(ActionEvent event)
    {
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (getStartDate() != null)
        {
            filterDate(getStartDate(), getEndDate(), getOrderTransactions());
        }
        
        setStartDate(null);
        setEndDate(null);
        setOrderTransactions(new ArrayList<>());
    }
    
    public void showReportByCategory(ActionEvent event)
    {
        filterCategory(getChosenCategory(), getOrderTransactions());
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (getStartDate() != null)
        {
            filterDate(getStartDate(), getEndDate(), getOrderTransactions());
        }
        
        setStartDate(null);
        setEndDate(null);
        setOrderTransactions(new ArrayList<>());
    }
    
    public void showReportByBrand(ActionEvent event)
    {
        filterBrand(getChosenBrand(), getOrderTransactions());
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (getStartDate() != null)
        {
            filterDate(getStartDate(), getEndDate(), getOrderTransactions());
        }
        
        setStartDate(null);
        setEndDate(null);
        setOrderTransactions(new ArrayList<>());
    }
    
    public void showReportByBundle(ActionEvent event)
    {
        filterBundle(getChosenBundle(), getOrderTransactions());
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (getStartDate() != null)
        {
            filterDate(getStartDate(), getEndDate(), getOrderTransactions());
        }
        
        setStartDate(null);
        setEndDate(null);
        setOrderTransactions(new ArrayList<>());
    }
    
    public void showReportByProduct(ActionEvent event)
    {
        filterProduct(getChosenProduct(), getOrderTransactions());
        //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Product Added to shopping cart", null));
        if (getStartDate() != null)
        {
            filterDate(getStartDate(), getEndDate(), getOrderTransactions());
        }
        
        setStartDate(null);
        setEndDate(null);
        setOrderTransactions(new ArrayList<>());
    }
    
    public void filterCategory(CategoryEntity category, List<OrderTransactionEntity> orders)
    {
        
    }
    
    public void filterBundle(BundleEntity bundle, List<OrderTransactionEntity> orders)
    {
        
    }
    
    public void filterBrand(BrandEntity brand, List<OrderTransactionEntity> orders)
    {
        for (OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity lineItem : order.getOrderLineItemEntities()) {
                ProductEntity product = lineItem.getProductEntity();
                if (product.getBrandEntity() == brand) {
                    this.getFilteredByBrand().add(lineItem);
                }
            }
        }
    }
    
    public void filterProduct(ProductEntity product, List<OrderTransactionEntity> orders)
    {
        
    }
    
    public void createBrandBarModel() {
        barModelBrand = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Sales Report by Brands");

        List<String> brandNames = new ArrayList<>();
        for (BrandEntity brand : this.getBrands()) {
            brandNames.add(brand.getName());
        }
        List<Number> values = new ArrayList<>();
        for (int i = 0; i < brandNames.size(); i++) {
            values.add(0);
        }
        
        String brandName = "";
        BigDecimal sale;
        Number currValue = 0;
        Number newValue = 0;
        int brandIndex = 0;
        for(OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity orderLineItem : order.getOrderLineItemEntities()) {
                if (orderLineItem.getProductEntity() != null)
                {
                    ProductEntity product = orderLineItem.getProductEntity();
                    brandName = product.getBrandEntity().getName();
                    //System.out.println(brandName);
                    brandIndex = brandNames.indexOf(brandName);
                    //System.out.println(brandIndex);
                    sale = orderLineItem.getSubTotal();
                    currValue = values.get(brandIndex);
                    newValue = currValue.doubleValue() + sale.doubleValue();
                    values.set(brandIndex, newValue);
                }
                else
                {
                    BundleEntity bundle = orderLineItem.getBundleEntity();
                    for (BundleLineItemEntity lineItem : bundle.getBundleLineItems())
                    {
                        brandName = lineItem.getProductEntity().getBrandEntity().getName();
                        brandIndex = brandNames.indexOf(brandName);
                        sale = lineItem.getSubTotal();
                        currValue = values.get(brandIndex);
                        newValue = currValue.doubleValue() + sale.doubleValue();
                        values.set(brandIndex, newValue);
                    }
                }
            }
        }
        System.out.println("REACHABLE?");
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>();
        boolean alternatingColor = true;
        for (int j = 0; j < values.size(); j++) {
            if (alternatingColor) {
                bgColor.add("rgba(54, 162, 235, 0.2)");
            } else {
                bgColor.add("rgba(153, 102, 255, 0.2)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBackgroundColor(bgColor);

        alternatingColor = true;
        List<String> borderColor = new ArrayList<>();
        for (int x = 0; x < values.size(); x++) {
            if (alternatingColor) {
                borderColor.add("rgb(54, 162, 235)");
            } else {
                borderColor.add("rgb(153, 102, 255)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();
        for (String name : brandNames) {
            labels.add(name);
        }
        data.setLabels(labels);
        barModelBrand.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        /*
        // disable animation
        Animation animation = new Animation() {};
        animation.setDuration(0);
        options.setAnimation(animation);
        */

        barModelBrand.setOptions(options);
    }
    
    public void createCategoryBarModel() {
        barModelCategory = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Sales Report by Category");

        List<String> categoryNames = new ArrayList<>();
        for (CategoryEntity category : this.getCategories()) {
            categoryNames.add(category.getName());
        }
        List<Number> values = new ArrayList<>();
        for (int i = 0; i < categoryNames.size(); i++) {
            values.add(0);
        }
        
        String categoryName = "";
        BigDecimal sale;
        Number currValue = 0;
        Number newValue = 0;
        int categoryIndex = 0;
        for(OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity orderLineItem : order.getOrderLineItemEntities()) {
                if (orderLineItem.getProductEntity() != null)
                {
                    ProductEntity product = orderLineItem.getProductEntity();
                    categoryName = product.getCategoryEntity().getName();
                    categoryIndex = categoryNames.indexOf(categoryName);
                    sale = orderLineItem.getSubTotal();
                    currValue = values.get(categoryIndex);
                    newValue = currValue.doubleValue() + sale.doubleValue();
                    values.set(categoryIndex, newValue);
                }
                else
                {
                    BundleEntity bundle = orderLineItem.getBundleEntity();
                    for (BundleLineItemEntity lineItem : bundle.getBundleLineItems())
                    {
                        categoryName = lineItem.getProductEntity().getCategoryEntity().getName();
                        categoryIndex = categoryNames.indexOf(categoryName);
                        sale = lineItem.getSubTotal();
                        currValue = values.get(categoryIndex);
                        newValue = currValue.doubleValue() + sale.doubleValue();
                        values.set(categoryIndex, newValue);
                    }
                }
            }
        }
        System.out.println("REACHABLE REACHABLE?");
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>();
        boolean alternatingColor = true;
        for (int j = 0; j < values.size(); j++) {
            if (alternatingColor) {
                bgColor.add("rgba(54, 162, 235, 0.2)");
            } else {
                bgColor.add("rgba(153, 102, 255, 0.2)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBackgroundColor(bgColor);

        alternatingColor = true;
        List<String> borderColor = new ArrayList<>();
        for (int x = 0; x < bgColor.size(); x++) {
            if (alternatingColor) {
                borderColor.add("rgb(54, 162, 235)");
            } else {
                borderColor.add("rgb(153, 102, 255)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();
        for (String name : categoryNames) {
            labels.add(name);
        }
        data.setLabels(labels);
        barModelCategory.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        /*
        // disable animation
        Animation animation = new Animation() {};
        animation.setDuration(0);
        options.setAnimation(animation);
        */

        barModelCategory.setOptions(options);
    }
    
    public void createProductBarModel() {
        barModelProduct = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Sales Report by Product");

        List<String> productNames = new ArrayList<>();
        for (ProductEntity product : this.getProducts()) {
            productNames.add(product.getName());
        }
        List<Number> values = new ArrayList<>(productNames.size());
        for (int i = 0; i < productNames.size(); i++) {
            values.add(0);
        }
        String productName = "";
        BigDecimal sale;
        Number currValue = 0;
        Number newValue = 0;
        int productIndex = 0;
        for(OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity orderLineItem : order.getOrderLineItemEntities()) {
                if (orderLineItem.getProductEntity() != null)
                {
                    ProductEntity product = orderLineItem.getProductEntity();
                    productName = product.getName();
                    productIndex = productNames.indexOf(productName);
                    sale = orderLineItem.getSubTotal();
                    currValue = values.get(productIndex);
                    newValue = currValue.doubleValue() + sale.doubleValue();
                    values.set(productIndex, newValue);
                }
                else
                {
                    BundleEntity bundle = orderLineItem.getBundleEntity();
                    for (BundleLineItemEntity lineItem : bundle.getBundleLineItems())
                    {
                        productName = lineItem.getProductEntity().getName();
                        productIndex = productNames.indexOf(productName);
                        sale = lineItem.getSubTotal();
                        currValue = values.get(productIndex);
                        newValue = currValue.doubleValue() + sale.doubleValue();
                        values.set(productIndex, newValue);
                    }
                }
            }
        }
        System.out.println("REACHABLE 2X?");
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>();
        boolean alternatingColor = true;
        for (int j = 0; j < values.size(); j++) {
            if (alternatingColor) {
                bgColor.add("rgba(54, 162, 235, 0.2)");
            } else {
                bgColor.add("rgba(153, 102, 255, 0.2)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBackgroundColor(bgColor);

        alternatingColor = true;
        List<String> borderColor = new ArrayList<>();
        for (int x = 0; x < bgColor.size(); x++) {
            if (alternatingColor) {
                borderColor.add("rgb(54, 162, 235)");
            } else {
                borderColor.add("rgb(153, 102, 255)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();
        for (String name : productNames) {
            labels.add(name);
        }
        data.setLabels(labels);
        barModelProduct.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        /*
        // disable animation
        Animation animation = new Animation() {};
        animation.setDuration(0);
        options.setAnimation(animation);
        */

        barModelProduct.setOptions(options);
    }
    
    public void createMonthlyBarModel() {
        barModelMonthly = new BarChartModel();
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Monthly Sales Report");

        List<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        
        List<Number> values = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            values.add(0);
        }
        
        BigDecimal sale;
        Number currValue = 0;
        Number newValue = 0;
        for(OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity orderLineItem : order.getOrderLineItemEntities()) {
                Date transactionDate = order.getTransactionDateTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(transactionDate);
                int transactionMonth = calendar.get(Calendar.MONTH);
                if (orderLineItem.getProductEntity() != null)
                {
                    ProductEntity product = orderLineItem.getProductEntity();
                    sale = orderLineItem.getSubTotal();
                    currValue = values.get(transactionMonth);
                    newValue = currValue.doubleValue() + sale.doubleValue();
                    values.set(transactionMonth, newValue);
                }
                else
                {
                    BundleEntity bundle = orderLineItem.getBundleEntity();
                    for (BundleLineItemEntity lineItem : bundle.getBundleLineItems())
                    {
                        sale = lineItem.getSubTotal();
                        currValue = values.get(transactionMonth);
                        newValue = currValue.doubleValue() + sale.doubleValue();
                        values.set(transactionMonth, newValue);
                    }
                }
            }
        }
        System.out.println("2X REACHABLE?");
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>();
        boolean alternatingColor = true;
        for (int j = 0; j < 12; j++) {
            if (alternatingColor) {
                bgColor.add("rgba(54, 162, 235, 0.2)");
            } else {
                bgColor.add("rgba(153, 102, 255, 0.2)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBackgroundColor(bgColor);

        alternatingColor = true;
        List<String> borderColor = new ArrayList<>();
        for (int x = 0; x < 12; x++) {
            if (alternatingColor) {
                borderColor.add("rgb(54, 162, 235)");
            } else {
                borderColor.add("rgb(153, 102, 255)");
            }
        }
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>();
        for (String month : months) {
            labels.add(month);
        }
        data.setLabels(labels);
        barModelMonthly.setData(data);

        //Options
        BarChartOptions options = new BarChartOptions();
        CartesianScales cScales = new CartesianScales();
        CartesianLinearAxes linearAxes = new CartesianLinearAxes();
        linearAxes.setOffset(true);
        CartesianLinearTicks ticks = new CartesianLinearTicks();
        ticks.setBeginAtZero(true);
        linearAxes.setTicks(ticks);
        cScales.addYAxesData(linearAxes);
        options.setScales(cScales);

        Title title = new Title();
        title.setDisplay(true);
        title.setText("Bar Chart");
        options.setTitle(title);

        Legend legend = new Legend();
        legend.setDisplay(true);
        legend.setPosition("top");
        LegendLabel legendLabels = new LegendLabel();
        legendLabels.setFontStyle("bold");
        legendLabels.setFontColor("#2980B9");
        legendLabels.setFontSize(24);
        legend.setLabels(legendLabels);
        options.setLegend(legend);

        /*
        // disable animation
        Animation animation = new Animation() {};
        animation.setDuration(0);
        options.setAnimation(animation);
        */

        barModelMonthly.setOptions(options);
    }

    public OrderTransactionSessionBeanLocal getOrderTransactionSessionBeanLocal() {
        return orderTransactionSessionBeanLocal;
    }

    public void setOrderTransactionSessionBeanLocal(OrderTransactionSessionBeanLocal orderTransactionSessionBeanLocal) {
        this.orderTransactionSessionBeanLocal = orderTransactionSessionBeanLocal;
    }

    public ProductEntitySessionBeanLocal getProductEntitySessionBeanLocal() {
        return productEntitySessionBeanLocal;
    }

    public void setProductEntitySessionBeanLocal(ProductEntitySessionBeanLocal productEntitySessionBeanLocal) {
        this.productEntitySessionBeanLocal = productEntitySessionBeanLocal;
    }

    public CategoryEntitySessionBeanLocal getCategoryEntitySessionBeanLocal() {
        return categoryEntitySessionBeanLocal;
    }

    public void setCategoryEntitySessionBeanLocal(CategoryEntitySessionBeanLocal categoryEntitySessionBeanLocal) {
        this.categoryEntitySessionBeanLocal = categoryEntitySessionBeanLocal;
    }

    public BrandEntitySessionBeanLocal getBrandEntitySessionBeanLocal() {
        return brandEntitySessionBeanLocal;
    }

    public void setBrandEntitySessionBeanLocal(BrandEntitySessionBeanLocal brandEntitySessionBeanLocal) {
        this.brandEntitySessionBeanLocal = brandEntitySessionBeanLocal;
    }

    public List<OrderLineItemEntity> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItemEntity> lineItems) {
        this.lineItems = lineItems;
    }

    public List<OrderTransactionEntity> getOrderTransactions() {
        return orderTransactions;
    }

    public void setOrderTransactions(List<OrderTransactionEntity> orderTransactions) {
        this.orderTransactions = orderTransactions;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

    public List<BrandEntity> getBrands() {
        return brands;
    }

    public void setBrands(List<BrandEntity> brands) {
        this.brands = brands;
    }

    public List<BundleEntity> getBundles() {
        return bundles;
    }

    public void setBundles(List<BundleEntity> bundles) {
        this.bundles = bundles;
    }

    public List<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(List<ProductEntity> products) {
        this.products = products;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public CategoryEntity getChosenCategory() {
        return chosenCategory;
    }

    public void setChosenCategory(CategoryEntity chosenCategory) {
        this.chosenCategory = chosenCategory;
    }

    public BrandEntity getChosenBrand() {
        return chosenBrand;
    }

    public void setChosenBrand(BrandEntity chosenBrand) {
        this.chosenBrand = chosenBrand;
    }

    public BundleEntity getChosenBundle() {
        return chosenBundle;
    }

    public void setChosenBundle(BundleEntity chosenBundle) {
        this.chosenBundle = chosenBundle;
    }

    public ProductEntity getChosenProduct() {
        return chosenProduct;
    }

    public void setChosenProduct(ProductEntity chosenProduct) {
        this.chosenProduct = chosenProduct;
    }

    public List<OrderLineItemEntity> getFilteredByBrand() {
        return filteredByBrand;
    }

    public void setFilteredByBrand(List<OrderLineItemEntity> filteredByBrand) {
        this.filteredByBrand = filteredByBrand;
    }

//    public PieChartModel getPieModel() {
//        return pieModel;
//    }
//
//    public void setPieModel(PieChartModel pieModel) {
//        this.pieModel = pieModel;
//    }

    public BarChartModel getBarModelBrand() {
        return barModelBrand;
    }

    public void setBarModelBrand(BarChartModel barModelBrand) {
        this.barModelBrand = barModelBrand;
    }

    public BarChartModel getBarModelCategory() {
        return barModelCategory;
    }

    public void setBarModelCategory(BarChartModel barModelCategory) {
        this.barModelCategory = barModelCategory;
    }

    public BarChartModel getBarModelProduct() {
        return barModelProduct;
    }

    public void setBarModelProduct(BarChartModel barModelProduct) {
        this.barModelProduct = barModelProduct;
    }

    public BarChartModel getBarModelMonthly() {
        return barModelMonthly;
    }

    public void setBarModelMonthly(BarChartModel barModelMonthly) {
        this.barModelMonthly = barModelMonthly;
    }
    
}
