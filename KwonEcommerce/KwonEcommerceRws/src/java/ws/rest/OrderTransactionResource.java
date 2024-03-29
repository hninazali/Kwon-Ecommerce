/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.OrderTransactionSessionBeanLocal;
import entity.BundleLineItemEntity;
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
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
import util.exception.NeedStaffPermissionException;
import util.exception.OrderTransactionAlreadyVoidedRefundedException;
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

                OrderTransactionEntity orderTransaction = orderTransactionSessionBean.retrieveOrderTransactionById(req.getOrderId());
                
                for (OrderLineItemEntity lineItem : orderTransaction.getOrderLineItemEntities())
                {
                    lineItem.getCustomerEntity().getOrderLineItemEntities().clear();
                    lineItem.getCustomerEntity().getOrderTransactionEntities().clear();
                    lineItem.getCustomerEntity().getGroupCartEntities().clear();
                    if (lineItem.getBundleEntity() != null)
                    {
                        lineItem.getBundleEntity().getCategoryEntities().clear();
                        lineItem.getBundleEntity().getTagEntities().clear();
                        
                        for (BundleLineItemEntity bundleLineItem : lineItem.getBundleEntity().getBundleLineItems())
                        {
                            bundleLineItem.getProductEntity().setCategoryEntity(null);
                            bundleLineItem.getProductEntity().setBrandEntity(null);
                            bundleLineItem.getProductEntity().getTagEntities().clear();
                        }
                    }
                    else
                    {
                        lineItem.getProductEntity().setCategoryEntity(null);
                        lineItem.getProductEntity().getTagEntities().clear();
                        lineItem.getProductEntity().setBrandEntity(null);
                    }
                }
                
                orderTransaction.getCustomerEntity().getGroupCartEntities().clear();
                orderTransaction.getCustomerEntity().getOrderLineItemEntities().clear();
                orderTransaction.getCustomerEntity().getOrderTransactionEntities().clear();
                orderTransaction.getCustomerEntity().setPersonalCartEntity(null);

                return Response.status(Response.Status.OK).entity(orderTransaction).build();
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
    
    @Path("viewOrderTransactionDetailsTemp/{orderId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response viewOrderTransactionDetailsTemp(@PathParam("orderId") Long orderId)
    {
        if (orderId != null)
        {
            try
            {
                //CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());

                OrderTransactionEntity orderTransaction = orderTransactionSessionBean.retrieveOrderTransactionById(orderId);
                System.out.println(orderTransaction.getOrderLineItemEntities().size());
                
                for (OrderLineItemEntity lineItem : orderTransaction.getOrderLineItemEntities())
                {
                    if (lineItem.getCustomerEntity().getOrderLineItemEntities() != null)
                    {
                        lineItem.getCustomerEntity().getOrderLineItemEntities().clear();
                    }
                    lineItem.getCustomerEntity().getOrderTransactionEntities().clear();
                    lineItem.getCustomerEntity().getGroupCartEntities().clear();
                    if (lineItem.getBundleEntity() != null)
                    {
                        lineItem.getBundleEntity().getCategoryEntities().clear();
                        lineItem.getBundleEntity().getTagEntities().clear();
                    }
                    else
                    {
                        lineItem.getProductEntity().setCategoryEntity(null);
                        lineItem.getProductEntity().getTagEntities().clear();
                        lineItem.getProductEntity().setBrandEntity(null);
                    }
                }
                
                orderTransaction.setCustomerEntity(null);

                return Response.status(Response.Status.OK).entity(orderTransaction).build();
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
    
    @Path("refundOrder")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refundOrder(ViewOrderDetailsReq req)
    {
        if (req != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
                boolean isRefunded = orderTransactionSessionBean.refundOrder(customer.getCustomerId(), req.getOrderId());
                
                if (isRefunded)
                {
                    return Response.status(Response.Status.OK).entity(true).build();
                }
                else
                {
                    return Response.status(Response.Status.BAD_REQUEST).entity("SO BADDD!!!!!!!").build();
                }
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(OrderTransactionNotFoundException | OrderTransactionAlreadyVoidedRefundedException | CustomerNotFoundException  ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity("WAHOOOOOO" + ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Void/Refund Order request").build();
        }
    }
    
    @Path("{orderId}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrderArrived(@QueryParam("username") String username, 
                                        @QueryParam("password") String password,
                                        @PathParam("orderId") Long orderId)
    {
        try
        {

            CustomerEntity customer = customerSessionBean.customerLogin(username, password);

            orderTransactionSessionBean.updateOrderArrived(orderId);

            return Response.status(Response.Status.OK).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(OrderTransactionNotFoundException | NeedStaffPermissionException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
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
