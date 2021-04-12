/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import ejb.session.stateless.OrderTransactionSessionBeanLocal;
import entity.OrderTransactionEntity;
import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import util.exception.OrderTransactionAlreadyVoidedRefundedException;
import util.exception.OrderTransactionNotFoundException;

/**
 *
 * @author User
 */
@Named(value = "refundManagedBean")
@ViewScoped
public class RefundManagedBean implements Serializable 
{

    @EJB(name = "OrderTransactionSessionBeanLocal")
    private OrderTransactionSessionBeanLocal orderTransactionSessionBeanLocal;
    
    private OrderTransactionEntity orderTransaction = new OrderTransactionEntity();
    private long orderId;
    private List<OrderTransactionEntity> orderTransactions;
    private List<OrderTransactionEntity> filteredOrderTransactions;

    
    

    /**
     * Creates a new instance of RefundManagedBean
     */
    public RefundManagedBean() 
    {
    }
    
    @PostConstruct
    public void postConstruct()
    {
        setOrderTransactions(orderTransactionSessionBeanLocal.retrieveAllOrderTransactions());
    }
    
//    public void refundOrderTransaction(ActionEvent event)
//    {
//        setOrderTransaction((OrderTransactionEntity)event.getComponent().getAttributes().get("orderTransactionToRefund"));
//        try {
//            OrderTransactionEntity selectedOrder = getOrderTransactionSessionBeanLocal().retrieveOrderTransactionById(getOrderId());
//            
//            boolean refunded = getOrderTransactionSessionBeanLocal().refundOrder(getOrderId());
//            
//            if (refunded)
//            {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Refund Accepted", null));
//            }
//            else
//            {
//                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Refund Rejected", null));
//            }
//        } catch (OrderTransactionNotFoundException | OrderTransactionAlreadyVoidedRefundedException ex) {
//            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Error Occurred", null));
//        }
//        
//    }

    public OrderTransactionSessionBeanLocal getOrderTransactionSessionBeanLocal() {
        return orderTransactionSessionBeanLocal;
    }

    public void setOrderTransactionSessionBeanLocal(OrderTransactionSessionBeanLocal orderTransactionSessionBeanLocal) {
        this.orderTransactionSessionBeanLocal = orderTransactionSessionBeanLocal;
    }

    public OrderTransactionEntity getOrderTransaction() {
        return orderTransaction;
    }

    public void setOrderTransaction(OrderTransactionEntity orderTransaction) {
        this.orderTransaction = orderTransaction;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public List<OrderTransactionEntity> getOrderTransactions() {
        return orderTransactions;
    }

    public void setOrderTransactions(List<OrderTransactionEntity> orderTransactions) {
        this.orderTransactions = orderTransactions;
    }
    
}
