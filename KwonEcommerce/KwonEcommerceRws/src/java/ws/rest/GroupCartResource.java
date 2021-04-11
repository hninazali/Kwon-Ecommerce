/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.GroupCartSessionBeanLocal;
import ejb.session.stateless.OrderLineItemSessionBeanLocal;
import entity.CustomerEntity;
import entity.GroupCartEntity;
import entity.OrderLineItemEntity;
import entity.OrderTransactionEntity;
import entity.PersonalCartEntity;
import java.util.ArrayList;
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
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.BundleNotFoundException;
import util.exception.CreateNewGroupCartException;
import util.exception.CreateNewOrderLineItemException;
import util.exception.CreateNewOrderTransactionException;
import util.exception.CustomerNotFoundException;
import util.exception.GroupActivityDetectedException;
import util.exception.GroupCartNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.OrderLineItemNotFoundException;
import util.exception.ProductNotFoundException;
import ws.datamodel.AddBundleToGroupCartReq;
import ws.datamodel.AddItemToGroupCartReq;
import ws.datamodel.CheckoutGroupCartReq;
import ws.datamodel.CreateGroupCartReq;
import ws.datamodel.LeaveGroupCartReq;
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

    OrderLineItemSessionBeanLocal orderLineItemSessionBean = lookupOrderLineItemSessionBeanLocal();

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
    public Response retrieveAllGroupCarts(Long customerId)
    {
        try
        {
            List<GroupCartEntity> groupCarts = groupCartSessionBean.retrieveCustomerGroupCartEntities(customerId);
            
            for (GroupCartEntity groupCart : groupCarts)
            {
                for (CustomerEntity customer : groupCart.getCustomerEntities())
                {
                    customer.getGroupCartEntities().clear();
                    customer.getOrderLineItemEntities().clear();
                    customer.getOrderTransactionEntities().clear();
                }
            }
            
            //==================================================================================
            //Might need to disassociate everything that the customers have inside the group
            //==================================================================================
            
            GenericEntity<List<GroupCartEntity>> genericEntity = new GenericEntity<List<GroupCartEntity>>(groupCarts) {
            };
            
            return Response.status(Response.Status.OK).entity(genericEntity).build();
        }
        catch (CustomerNotFoundException ex)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveGroupMembers/{groupId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllGroupCarts(@QueryParam("username") String username, 
                                @QueryParam("password") String password,
                                    @PathParam("groupId") Long groupId)
    {
        if (groupId != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(username, password);
                
                GroupCartEntity groupCart = groupCartSessionBean.retrieveGroupCartById(groupId);
                
                List<CustomerEntity> tempMembers = groupCart.getCustomerEntities();
                
                List<CustomerEntity> members = new ArrayList<>();
                members.add(groupCart.getGroupOwner());
                
                for (CustomerEntity member : tempMembers)
                {
                    member.getGroupCartEntities().clear();
                    member.getOrderLineItemEntities().clear();
                    member.getOrderTransactionEntities().clear();
                    
                    members.add(member);
                }
                
                GenericEntity<List<CustomerEntity>> genericEntity = new GenericEntity<List<CustomerEntity>>(members){
                };
                
                return Response.status(Response.Status.OK).entity(genericEntity).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch (GroupCartNotFoundException ex)
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
        
    }
    
    @Path("retrieveGroupOrderLineItems")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllPersonalOrderLineItems(LeaveGroupCartReq req)
    {
        if (req != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
                
                GroupCartEntity groupCart = groupCartSessionBean.retrieveGroupCartById(req.getGroupCartId());
                List<OrderLineItemEntity> orderLineItems = groupCart.getOrderLineItemEntities();
                
                for (OrderLineItemEntity lineItem : orderLineItems)
                {
                    lineItem.getCustomerEntity().getOrderLineItemEntities().clear();
                    lineItem.getCustomerEntity().getOrderTransactionEntities().clear();
                    lineItem.getCustomerEntity().getGroupCartEntities().clear();
                }
            
                GenericEntity<List<OrderLineItemEntity>> genericEntity = new GenericEntity<List<OrderLineItemEntity>>(orderLineItems){
                };

                return Response.status(Response.Status.OK).entity(genericEntity).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch (GroupCartNotFoundException ex)
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }
    
    @Path("addItemToGroupCart")
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

                if (groupCartSessionBean.isInsideCart(req.getGroupCart().getGroupCartId(), customer.getCustomerId(), req.getProduct()))
                {
                    OrderLineItemEntity lineItemEntity = groupCartSessionBean.addQuantity(req.getGroupCart().getGroupCartId(), customer.getCustomerId(), req.getProduct(), req.getQuantity());

                    return Response.status(Response.Status.OK).entity(lineItemEntity.getOrderLineItemId()).build();
                }
                else
                {

                    OrderLineItemEntity temp = orderLineItemSessionBean.createLineItemForCart(req.getProduct().getProductId(), req.getQuantity());

                    OrderLineItemEntity lineItem = groupCartSessionBean.addNewOrderLineItemToCart(customer.getCustomerId(), req.getGroupCart().getGroupCartId(), temp);

                    return Response.status(Response.Status.OK).entity(lineItem.getOrderLineItemId()).build();
                }
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(CreateNewOrderLineItemException | ProductNotFoundException | CustomerNotFoundException | InputDataValidationException | GroupCartNotFoundException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }
    
    @Path("addBundleToGroupCart")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBundleToGroupCart(AddBundleToGroupCartReq req)
    {
        if (req != null)
        {
            try
            {
                
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());

                if (groupCartSessionBean.bundleIsInsideCart(req.getGroupCart().getGroupCartId(), customer.getCustomerId(), req.getBundle()))
                {
                    OrderLineItemEntity lineItemEntity = groupCartSessionBean.addQuantityBundle(req.getGroupCart().getGroupCartId(), customer.getCustomerId(), req.getBundle(), req.getQuantity());

                    return Response.status(Response.Status.OK).entity(lineItemEntity.getOrderLineItemId()).build();
                }
                else
                {

                    OrderLineItemEntity temp = orderLineItemSessionBean.createLineItemForCartBundle(req.getBundle().getBundleId(), req.getQuantity());

                    OrderLineItemEntity lineItem = groupCartSessionBean.addNewOrderLineItemToCart(customer.getCustomerId(), req.getGroupCart().getGroupCartId(), temp);

                    return Response.status(Response.Status.OK).entity(lineItem.getOrderLineItemId()).build();
                }
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(CreateNewOrderLineItemException | BundleNotFoundException | CustomerNotFoundException | InputDataValidationException | GroupCartNotFoundException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }
    
    @Path("updateGroupLineItem")
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
    
    @Path("removeGroupLineItem")
    @POST
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
    
    @Path("createNewGroupCart")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroupCart(CreateGroupCartReq req)
    {
        if (req != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
                
                GroupCartEntity groupCart = groupCartSessionBean.createNewGroupCart(customer.getCustomerId(), req.getName(), req.getUsernames());
                
                for (CustomerEntity customerTemp : groupCart.getCustomerEntities())
                {
                    customerTemp.getGroupCartEntities().clear();
                    customerTemp.getOrderLineItemEntities().clear();
                    customerTemp.getOrderTransactionEntities().clear();
                }
                
                return Response.status(Response.Status.OK).entity(groupCart.getGroupCartId()).build();
            }
            catch (InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch (InputDataValidationException | CreateNewGroupCartException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }

    @Path("leaveGroupCart")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response leaveGroupCart(LeaveGroupCartReq req)
    {
        if (req != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
                
                groupCartSessionBean.leaveGroup(req.getGroupCartId(), customer.getCustomerId());
                
                return Response.status(Response.Status.OK).build();
            }
            catch (InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch (GroupCartNotFoundException | CustomerNotFoundException | GroupActivityDetectedException  ex)
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

    private OrderLineItemSessionBeanLocal lookupOrderLineItemSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (OrderLineItemSessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/OrderLineItemSessionBean!ejb.session.stateless.OrderLineItemSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
