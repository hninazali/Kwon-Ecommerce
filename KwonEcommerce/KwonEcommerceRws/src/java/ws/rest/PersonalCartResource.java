/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.PersonalCartSessionBeanLocal;
import entity.CustomerEntity;
import static entity.CustomerEntity_.customerId;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import entity.PersonalCartEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.PersonalCartNotFoundException;
import ws.datamodel.AddItemToPersonalCartReq;
import ws.datamodel.CheckoutPersonalCartReq;
import ws.datamodel.RemoveOrderLineItemReq;
import ws.datamodel.UpdateOrderLineItemReq;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("PersonalCart")
public class PersonalCartResource 
{

    CustomerSessionBeanLocal customerSessionBean = lookupCustomerSessionBeanLocal();

    PersonalCartSessionBeanLocal personalCartSessionBean = lookupPersonalCartSessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of PersonalCartResource
     */
    public PersonalCartResource() {
    }
    
    @Path("retrieveAllPersonalCarts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllPersonalCarts(Long customerId)
    {
        try
        {
            PersonalCartEntity personalCart = personalCartSessionBean.retrievePersonalCartEntity(customerId);
            
            return Response.status(Response.Status.OK).entity(personalCart).build();
        }
        catch (CustomerNotFoundException ex)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveAllPersonalOrderLineItems")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllPersonalOrderLineItems(Long customerId)
    {
        try
        {
            PersonalCartEntity personalCart = personalCartSessionBean.retrievePersonalCartEntity(customerId);
            List<OrderLineItemEntity> orderLineItems = personalCart.getOrderLineItemEntities();
            
            return Response.status(Response.Status.OK).entity(orderLineItems).build();
        }
        catch (CustomerNotFoundException ex)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItemToPersonalCart(AddItemToPersonalCartReq req)
    {
        if (req != null)
        {
            try
            {
                
            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
            
            OrderLineItemEntity lineItem = personalCartSessionBean.addNewOrderLineItemToCart(customer.getCustomerId(), req.getLineItem());
            
            return Response.status(Response.Status.OK).entity(lineItem.getOrderLineItemId()).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(CreateNewOrderLineItemException | CustomerNotFoundException | InputDataValidationException ex)
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
    public Response updateOrderLineItem(UpdateOrderLineItemReq req)
    {
        if (req != null)
        {
            try
            {
                
            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
            
            OrderLineItemEntity lineItem = personalCartSessionBean.updateOrderLineItem(customer.getCustomerId(), req.getLineItem(), req.getNewQty());
            
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
    
    @Path("checkOutPersonalCart")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkOutCart(CheckoutPersonalCartReq req)
    {
        if (req != null)
        {
            try
            {
                
            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
            
            OrderTransactionEntity orderTransaction = personalCartSessionBean.checkOutCart(customer.getCustomerId(), req.getPersonalCart().getPersonalCartId());
            
            return Response.status(Response.Status.OK).entity(orderTransaction.getOrderTransactionId()).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(PersonalCartNotFoundException | CreateNewOrderTransactionException | CustomerNotFoundException  ex)
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
    public Response removeOrderLineItem(RemoveOrderLineItemReq req)
    {
        if (req != null)
        {
            try
            {
                
            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
            
            personalCartSessionBean.removeOrderLineItem(customer.getCustomerId(), req.getLineItem());
            
            return Response.status(Response.Status.OK).build();
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

    private PersonalCartSessionBeanLocal lookupPersonalCartSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (PersonalCartSessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/PersonalCartSessionBean!ejb.session.stateless.PersonalCartSessionBeanLocal");
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
