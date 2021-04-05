/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.GroupCartSessionBeanLocal;
import entity.CustomerEntity;
import entity.GroupCartEntity;
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
import javax.ws.rs.PUT;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupCartNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OrderLineItemNotFoundException;
import ws.datamodel.AddItemToGroupCartReq;
import ws.datamodel.CheckoutGroupCartReq;
import ws.datamodel.RemoveGroupLineItemReq;
import ws.datamodel.UpdateGroupLineItemReq;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("GroupCart")
public class GroupCartResource 
{

    GroupCartSessionBeanLocal groupCartSessionBean = lookupGroupCartSessionBeanLocal();

    CustomerSessionBeanLocal customerSessionBean = lookupCustomerSessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GroupCartResource
     */
    public GroupCartResource() {
    }
    
    @Path("retrieveAllGroupCarts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllPersonalCarts(Long customerId)
    {
        try
        {
            List<GroupCartEntity> groupCarts = groupCartSessionBean.retrieveCustomerGroupCartEntities(customerId);
            
            GenericEntity<List<GroupCartEntity>> genericEntity = new GenericEntity<List<GroupCartEntity>>(groupCarts) {
            };
            
            //==================================================================================
            //Might need to disassociate everything that the customers have inside the group
            //==================================================================================
            
            return Response.status(Response.Status.OK).entity(genericEntity).build();
        }
        catch (CustomerNotFoundException ex)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItemToGroupCart(AddItemToGroupCartReq req)
    {
        if (req != null)
        {
            try
            {
                
            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
            
            OrderLineItemEntity lineItem = groupCartSessionBean.addNewOrderLineItemToCart(customer.getCustomerId(), req.getGroupCart().getGroupCartId(), req.getLineItem());
            
            return Response.status(Response.Status.OK).entity(lineItem.getOrderLineItemId()).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(CreateNewOrderLineItemException | CustomerNotFoundException | InputDataValidationException | GroupCartNotFoundException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrderLineItem(UpdateGroupLineItemReq req)
    {
        if (req != null)
        {
            try
            {
                
            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
            
            OrderLineItemEntity lineItem = groupCartSessionBean.updateOrderLineItem(customer.getCustomerId(), req.getLineItem(), req.getNewQty());
            
            return Response.status(Response.Status.OK).entity(lineItem.getOrderLineItemId()).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(OrderLineItemNotFoundException | CustomerNotFoundException  ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }
    
    @Path("checkOutCart")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkOutCart(CheckoutGroupCartReq req)
    {
        if (req != null)
        {
            try
            {
                
            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
            
            OrderTransactionEntity orderTransaction = groupCartSessionBean.checkOutCart(customer.getCustomerId(), req.getGroupCart().getGroupCartId());
            
            return Response.status(Response.Status.OK).entity(orderTransaction.getOrderTransactionId()).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(GroupCartNotFoundException | CreateNewOrderTransactionException | CustomerNotFoundException  ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }
    
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeOrderLineItem(RemoveGroupLineItemReq req)
    {
        if (req != null)
        {
            try
            {
                
            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
            
            groupCartSessionBean.removeOrderLineItem(customer.getCustomerId(), req.getGroupCart().getGroupCartId(), req.getLineItem());
            
            return Response.status(Response.Status.OK).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(OrderLineItemNotFoundException | CustomerNotFoundException | GroupCartNotFoundException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
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

    private GroupCartSessionBeanLocal lookupGroupCartSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (GroupCartSessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/GroupCartSessionBean!ejb.session.stateless.GroupCartSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}