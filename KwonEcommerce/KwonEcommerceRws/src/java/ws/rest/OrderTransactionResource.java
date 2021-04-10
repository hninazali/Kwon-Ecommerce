/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.OrderTransactionSessionBeanLocal;
import entity.CustomerEntity;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OrderTransactionNotFoundException;
import ws.datamodel.ViewOrderDetailsReq;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("OrderTransaction")
public class OrderTransactionResource 
{

    CustomerSessionBeanLocal customerSessionBean = lookupCustomerSessionBeanLocal();

    OrderTransactionSessionBeanLocal orderTransactionSessionBean = lookupOrderTransactionSessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of OrderTransactionResource
     */
    public OrderTransactionResource() {
    }
    
    @Path("retrieveAllOrderTransactions")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllOrderTransactions(@QueryParam("username") String username, 
                                @QueryParam("password") String password)
    {
        try
        {
            CustomerEntity customer = customerSessionBean.customerLogin(username, password);
            
            List<OrderTransactionEntity> orderTransactions = orderTransactionSessionBean.retrieveOrderTransactionsByCustomer(customer.getCustomerId());
            
            for (OrderTransactionEntity order : orderTransactions)
            {
                order.getOrderLineItemEntities().clear();
                order.setCustomerEntity(null);
            }
            
            GenericEntity<List<OrderTransactionEntity>> genericEntity = new GenericEntity<List<OrderTransactionEntity>>(orderTransactions) {
            };
            
            return Response.status(Response.Status.OK).entity(genericEntity).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(CustomerNotFoundException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    @Path("viewOrderTransactionDetails")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewOrderTransactionDetails(ViewOrderDetailsReq req)
    {
        if (req != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());

                OrderTransactionEntity orderTransactions = orderTransactionSessionBean.retrieveOrderTransactionById(req.getOrderId());
                
                List<OrderLineItemEntity> lineItems = orderTransactions.getOrderLineItemEntities();

                
                for (OrderLineItemEntity lineItem : lineItems)
                {
                    lineItem.getCustomerEntity().getOrderLineItemEntities().clear();
                    lineItem.getCustomerEntity().getOrderTransactionEntities().clear();
                    lineItem.getCustomerEntity().getGroupCartEntities().clear();
                }

                GenericEntity<List<OrderLineItemEntity>> genericEntity = new GenericEntity<List<OrderLineItemEntity>>(lineItems) {
                };

                return Response.status(Response.Status.OK).entity(genericEntity).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(OrderTransactionNotFoundException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }

    private OrderTransactionSessionBeanLocal lookupOrderTransactionSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (OrderTransactionSessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/OrderTransactionSessionBean!ejb.session.stateless.OrderTransactionSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private CustomerSessionBeanLocal lookupCustomerSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CustomerSessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/CustomerSessionBean!ejb.session.stateless.CustomerSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
