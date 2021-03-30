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
    
    private PieChartModel pieModel;
    private BarChartModel barModelBrand;
    private BarChartModel barModelCategory;
    private BarChartModel barModelProduct;
    private BarChartModel barModelMonthly;
    
    

    /**
     * Creates a new instance of SalesReportManagedBean
     */
    public SalesReportManagedBean() {
    }
    
    @PostConstruct
    public void postConstruct()
    {
        setOrderTransactions(getOrderTransactionSessionBeanLocal().retrieveAllOrderTransactions());
        setCategories(getCategoryEntitySessionBeanLocal().retrieveAllCategories());
        setBrands(getBrandEntitySessionBeanLocal().retrieveAllBrands());
        setProducts(getProductEntitySessionBeanLocal().retrieveAllProducts());
        
        this.createBrandBarModel();
        this.createCategoryBarModel();
        this.createMonthlyBarModel();
        this.createProductBarModel();
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
        setBarModelBrand(new BarChartModel());
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Sales Report by Brands");

        List<String> brandNames = new ArrayList<>();
        for (BrandEntity brand : this.getBrands()) {
            brandNames.add(brand.getName());
        }
        List<Number> values = new ArrayList<>(brandNames.size());
        for (int i = 0; i < values.size(); i++) {
            values.set(i, 0);
        }
        
        for(OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity orderLineItem : order.getOrderLineItemEntities()) {
                ProductEntity product = orderLineItem.getProductEntity();
                String brandName = product.getBrandEntity().getName();
                int brandIndex = brandNames.indexOf(brandName);
                BigDecimal sale = orderLineItem.getSubTotal();
                Number currValue = values.get(brandIndex);
                Number newValue = currValue.doubleValue() + sale.doubleValue();
                values.set(brandIndex, newValue);
            }
        }
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>(values.size());
        boolean alternatingColor = true;
        for (int j = 0; j < bgColor.size(); j++) {
            if (alternatingColor) {
                bgColor.add("rgba(54, 162, 235, 0.2)");
            } else {
                bgColor.add("rgba(153, 102, 255, 0.2)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBackgroundColor(bgColor);

        alternatingColor = true;
        List<String> borderColor = new ArrayList<>(bgColor.size());
        for (int x = 0; x < borderColor.size(); x++) {
            if (alternatingColor) {
                borderColor.add("rgb(54, 162, 235)");
            } else {
                borderColor.add("rgb(153, 102, 255)");
            }
        }
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>(brandNames.size());
        for (String name : brandNames) {
            labels.add(name);
        }
        data.setLabels(labels);
        getBarModelBrand().setData(data);

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

        getBarModelBrand().setOptions(options);
    }
    
    public void createCategoryBarModel() {
        setBarModelCategory(new BarChartModel());
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Sales Report by Category");

        List<String> categoryNames = new ArrayList<>();
        for (CategoryEntity category : this.getCategories()) {
            categoryNames.add(category.getName());
        }
        List<Number> values = new ArrayList<>(categoryNames.size());
        for (int i = 0; i < values.size(); i++) {
            values.set(i, 0);
        }
        
        for(OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity orderLineItem : order.getOrderLineItemEntities()) {
                ProductEntity product = orderLineItem.getProductEntity();
                String categoryName = product.getCategoryEntity().getName();
                int categoryIndex = categoryNames.indexOf(categoryName);
                BigDecimal sale = orderLineItem.getSubTotal();
                Number currValue = values.get(categoryIndex);
                Number newValue = currValue.doubleValue() + sale.doubleValue();
                values.set(categoryIndex, newValue);
            }
        }
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>(values.size());
        boolean alternatingColor = true;
        for (int j = 0; j < bgColor.size(); j++) {
            if (alternatingColor) {
                bgColor.add("rgba(54, 162, 235, 0.2)");
            } else {
                bgColor.add("rgba(153, 102, 255, 0.2)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBackgroundColor(bgColor);

        alternatingColor = true;
        List<String> borderColor = new ArrayList<>(bgColor.size());
        for (int x = 0; x < borderColor.size(); x++) {
            if (alternatingColor) {
                borderColor.add("rgb(54, 162, 235)");
            } else {
                borderColor.add("rgb(153, 102, 255)");
            }
        }
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>(categoryNames.size());
        for (String name : categoryNames) {
            labels.add(name);
        }
        data.setLabels(labels);
        getBarModelCategory().setData(data);

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

        getBarModelCategory().setOptions(options);
    }
    
    public void createProductBarModel() {
        setBarModelProduct(new BarChartModel());
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Sales Report by Category");

        List<String> productNames = new ArrayList<>();
        for (ProductEntity product : this.getProducts()) {
            productNames.add(product.getName());
        }
        List<Number> values = new ArrayList<>(productNames.size());
        for (int i = 0; i < values.size(); i++) {
            values.set(i, 0);
        }
        
        for(OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity orderLineItem : order.getOrderLineItemEntities()) {
                ProductEntity product = orderLineItem.getProductEntity();
                String categoryName = product.getCategoryEntity().getName();
                int categoryIndex = productNames.indexOf(categoryName);
                BigDecimal sale = orderLineItem.getSubTotal();
                Number currValue = values.get(categoryIndex);
                Number newValue = currValue.doubleValue() + sale.doubleValue();
                values.set(categoryIndex, newValue);
            }
        }
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>(values.size());
        boolean alternatingColor = true;
        for (int j = 0; j < bgColor.size(); j++) {
            if (alternatingColor) {
                bgColor.add("rgba(54, 162, 235, 0.2)");
            } else {
                bgColor.add("rgba(153, 102, 255, 0.2)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBackgroundColor(bgColor);

        alternatingColor = true;
        List<String> borderColor = new ArrayList<>(bgColor.size());
        for (int x = 0; x < borderColor.size(); x++) {
            if (alternatingColor) {
                borderColor.add("rgb(54, 162, 235)");
            } else {
                borderColor.add("rgb(153, 102, 255)");
            }
        }
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>(productNames.size());
        for (String name : productNames) {
            labels.add(name);
        }
        data.setLabels(labels);
        getBarModelProduct().setData(data);

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

        getBarModelProduct().setOptions(options);
    }
    
    public void createMonthlyBarModel() {
        setBarModelMonthly(new BarChartModel());
        ChartData data = new ChartData();

        BarChartDataSet barDataSet = new BarChartDataSet();
        barDataSet.setLabel("Sales Report by Category");

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
        
        List<Number> values = new ArrayList<>(12);
        for (int i = 0; i < values.size(); i++) {
            values.set(i, 0);
        }
        
        for(OrderTransactionEntity order : this.getOrderTransactions()) {
            for (OrderLineItemEntity orderLineItem : order.getOrderLineItemEntities()) {
                Date transactionDate = order.getTransactionDateTime();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(transactionDate);
                int transactionMonth = calendar.get(Calendar.MONTH);
                ProductEntity product = orderLineItem.getProductEntity();
                BigDecimal sale = orderLineItem.getSubTotal();
                Number currValue = values.get(transactionMonth);
                Number newValue = currValue.doubleValue() + sale.doubleValue();
                values.set(transactionMonth, newValue);
            }
        }
        barDataSet.setData(values);

        List<String> bgColor = new ArrayList<>(values.size());
        boolean alternatingColor = true;
        for (int j = 0; j < bgColor.size(); j++) {
            if (alternatingColor) {
                bgColor.add("rgba(54, 162, 235, 0.2)");
            } else {
                bgColor.add("rgba(153, 102, 255, 0.2)");
            }
            alternatingColor = !alternatingColor;
        }
        barDataSet.setBackgroundColor(bgColor);

        alternatingColor = true;
        List<String> borderColor = new ArrayList<>(bgColor.size());
        for (int x = 0; x < borderColor.size(); x++) {
            if (alternatingColor) {
                borderColor.add("rgb(54, 162, 235)");
            } else {
                borderColor.add("rgb(153, 102, 255)");
            }
        }
        barDataSet.setBorderColor(borderColor);
        barDataSet.setBorderWidth(1);

        data.addChartDataSet(barDataSet);

        List<String> labels = new ArrayList<>(months.size());
        for (String month : months) {
            labels.add(month);
        }
        data.setLabels(labels);
        getBarModelMonthly().setData(data);

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

        getBarModelMonthly().setOptions(options);
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

    public PieChartModel getPieModel() {
        return pieModel;
    }

    public void setPieModel(PieChartModel pieModel) {
        this.pieModel = pieModel;
    }

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
