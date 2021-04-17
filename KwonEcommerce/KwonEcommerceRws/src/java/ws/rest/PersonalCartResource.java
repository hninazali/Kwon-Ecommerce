/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import ejb.session.stateless.OrderTransactionSessionBeanLocal;
import ejb.session.stateless.PersonalCartSessionBeanLocal;
import entity.CustomerEntity;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.BundleInsufficientQuantityOnHandException;
import util.exception.BundleNotFoundException;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CreditCardNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.PersonalCartNotFoundException;
import util.exception.ProductInsufficientQuantityOnHandException;
import util.exception.ProductNotFoundException;
import util.exception.TooMuchQuantityException;
import ws.datamodel.AddBundleToPersonalCartReq;
import ws.datamodel.AddItemToPersonalCartReq;
import ws.datamodel.CheckoutPersonalCartReq;
import ws.datamodel.UpdateOrderLineItemReq;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("PersonalCart")
public class PersonalCartResource 
{

    OrderTransactionSessionBeanLocal orderTransactionSessionBean = lookupOrderTransactionSessionBeanLocal();

    OrderLineItemSessionBeanLocal orderLineItemSessionBean = lookupOrderLineItemSessionBeanLocal();

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
    public Response retrieveAllPersonalOrderLineItems(@QueryParam("username") String username, 
                                        @QueryParam("password") String password)
    {
        try
        {
            CustomerEntity customer = customerSessionBean.customerLogin(username, password);
            PersonalCartEntity personalCart = personalCartSessionBean.retrievePersonalCartEntity(customer.getCustomerId());
            List<OrderLineItemEntity> orderLineItems = personalCart.getOrderLineItemEntities();
            
            for (OrderLineItemEntity lineItem : orderLineItems)
            {
//                lineItem.getCustomerEntity().getOrderLineItemEntities().clear();
//                lineItem.getCustomerEntity().getOrderTransactionEntities().clear();
//                lineItem.getCustomerEntity().getGroupCartEntities().clear();
//                lineItem.getCustomerEntity().setPersonalCartEntity(null);
                lineItem.setCustomerEntity(null);
                if (lineItem.getBundleEntity() != null)
                {
                    lineItem.getBundleEntity().getCategoryEntities().clear();
                    lineItem.getBundleEntity().getTagEntities().clear();
                    lineItem.getBundleEntity().getBundleLineItems().clear();
                }
                else
                {
                    lineItem.getProductEntity().setCategoryEntity(null);
                    lineItem.getProductEntity().getTagEntities().clear();
                    lineItem.getProductEntity().setBrandEntity(null);
                }
            }
            
            GenericEntity<List<OrderLineItemEntity>> genericEntity = new GenericEntity<List<OrderLineItemEntity>>(orderLineItems){
            };
            
            return Response.status(Response.Status.OK).entity(genericEntity).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch (CustomerNotFoundException ex)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("addItemToCart")
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

                if (personalCartSessionBean.isInsideCart(customer.getCustomerId(), req.getProduct()))
                {
                    OrderLineItemEntity lineItemEntity = personalCartSessionBean.addQuantity(customer.getCustomerId(), req.getProduct(), req.getQuantity());

                    return Response.status(Response.Status.OK).entity(lineItemEntity.getOrderLineItemId()).build();
                }
                else
                {

                    OrderLineItemEntity temp = orderLineItemSessionBean.createLineItemForCart(req.getProduct().getProductId(), req.getQuantity());

                    OrderLineItemEntity lineItem = personalCartSessionBean.addNewOrderLineItemToCart(customer.getCustomerId(), temp);

                    return Response.status(Response.Status.OK).entity(lineItem.getOrderLineItemId()).build();
                }
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(CreateNewOrderLineItemException | TooMuchQuantityException | CustomerNotFoundException | InputDataValidationException | ProductNotFoundException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Add Item To Personal Cart request").build();
        }
    }
    
    @Path("addBundleToCart")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBundleToPersonalCart(AddBundleToPersonalCartReq req)
    {
        if (req != null)
        {
            try
            {
                
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());

                if (personalCartSessionBean.bundleIsInsideCart(customer.getCustomerId(), req.getBundle()))
                {
                    OrderLineItemEntity lineItemEntity = personalCartSessionBean.addQuantityBundle(customer.getCustomerId(), req.getBundle(), req.getQuantity());

                    return Response.status(Response.Status.OK).entity(lineItemEntity.getOrderLineItemId()).build();
                }
                else
                {

                    OrderLineItemEntity temp = orderLineItemSessionBean.createLineItemForCartBundle(req.getBundle().getBundleId(), req.getQuantity());

                    OrderLineItemEntity lineItem = personalCartSessionBean.addNewOrderLineItemToCart(customer.getCustomerId(), temp);

                    return Response.status(Response.Status.OK).entity(lineItem.getOrderLineItemId()).build();
                }
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(CreateNewOrderLineItemException | TooMuchQuantityException | CustomerNotFoundException | InputDataValidationException | BundleNotFoundException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Add Bundle To Cart Request").build();
        }
    }
    
    @Path("updateOrderLineItem")
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
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid update Order Line Item Request").build();
        }
    }
    
    @Path("checkOutPersonalCart")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkOutCart(CheckoutPersonalCartReq req)
    {
        try
        {

            CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());

            OrderTransactionEntity orderTransaction = personalCartSessionBean.checkOutCart(customer.getCustomerId(), customer.getPersonalCartEntity().getPersonalCartId(), req.getCreditCardId());

            return Response.status(Response.Status.OK).entity(orderTransaction.getOrderTransactionId()).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(PersonalCartNotFoundException | CreditCardNotFoundException | CreateNewOrderTransactionException | CustomerNotFoundException | BundleNotFoundException | BundleInsufficientQuantityOnHandException | ProductNotFoundException | ProductInsufficientQuantityOnHandException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    @Path("{lineItemId}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response removeOrderLineItem(@QueryParam("username") String username, 
                                        @QueryParam("password") String password,
                                        @PathParam("lineItemId") Long lineItemId)
    {
        try
        {

            CustomerEntity customer = customerSessionBean.customerLogin(username, password);

            personalCartSessionBean.removeOrderLineItem(customer.getCustomerId(), lineItemId);

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

    private OrderLineItemSessionBeanLocal lookupOrderLineItemSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (OrderLineItemSessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/OrderLineItemSessionBean!ejb.session.stateless.OrderLineItemSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
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
}
