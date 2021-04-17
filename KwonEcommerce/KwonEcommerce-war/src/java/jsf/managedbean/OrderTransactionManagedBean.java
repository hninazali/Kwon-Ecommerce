/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.GroupCartSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import ejb.session.stateless.OrderTransactionSessionBeanLocal;
import ejb.session.stateless.PersonalCartSessionBeanLocal;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
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
import util.exception.OrderTransactionNotFoundException;

/**
 *
 * @author User
 */
@Named(value = "orderTransactionManagedBean")
@ViewScoped
public class OrderTransactionManagedBean implements Serializable
{

    @EJB(name = "OrderTransactionSessionBeanLocal")
    private OrderTransactionSessionBeanLocal orderTransactionSessionBeanLocal;

    @EJB(name = "OrderLineItemSessionBeanLocal")
    private OrderLineItemSessionBeanLocal orderLineItemSessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB(name = "PersonalCartSessionBeanLocal")
    private PersonalCartSessionBeanLocal personalCartSessionBeanLocal;

    @EJB(name = "GroupCartSessionBeanLocal")
    private GroupCartSessionBeanLocal groupCartSessionBeanLocal;
    
    private List<OrderTransactionEntity> orders;
    private OrderTransactionEntity selectedOrder;
    private List<OrderLineItemEntity> lineItems;
    

    /**
     * Creates a new instance of OrderTransactionManagedBean
     */
    public OrderTransactionManagedBean() 
    {
        orders = new ArrayList<>();
        lineItems = new ArrayList<>();
    }
    
    @PostConstruct
    public void postConstruct()
    {
        orders = orderTransactionSessionBeanLocal.retrieveAllOrderTransactions();
    }
    
    public void updateShippingStatus(ActionEvent event)
    {
        try
        {
            selectedOrder = (OrderTransactionEntity) event.getComponent().getAttributes().get("orderToUpdate");
            OrderTransactionEntity order = orderTransactionSessionBeanLocal.retrieveOrderTransactionById(selectedOrder.getOrderTransactionId());    
            orderTransactionSessionBeanLocal.updateShippingStatus(order);
            orders = orderTransactionSessionBeanLocal.retrieveAllOrderTransactions();
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Shipment Status Successfully updated!!!", null));
        }
        catch (OrderTransactionNotFoundException ex)
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An unexpected error has occurred: " + ex.getMessage(), null));
        }
    }

    public List<OrderTransactionEntity> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderTransactionEntity> orders) {
        this.orders = orders;
    }

    public OrderTransactionEntity getSelectedOrder() {
        return selectedOrder;
    }

    public void setSelectedOrder(OrderTransactionEntity selectedOrder) {
        this.selectedOrder = selectedOrder;
    }

    public List<OrderLineItemEntity> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<OrderLineItemEntity> lineItems) {
        this.lineItems = lineItems;
    }
    
}
