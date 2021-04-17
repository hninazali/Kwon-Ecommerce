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
import java.io.Serializable;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;

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
    

    /**
     * Creates a new instance of OrderTransactionManagedBean
     */
    public OrderTransactionManagedBean() {
    }
    
}
