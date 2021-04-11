/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.CustomerEntity;
import static entity.OrderTransactionEntity_.staffEntity;
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
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.CustomerNotFoundException;
import util.exception.CustomerUsernameExistException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateCustomerException;
import ws.datamodel.CheckUsernameReq;
import ws.datamodel.UpdateCustomerReq;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("Customer")
public class CustomerResource 
{

    CustomerSessionBeanLocal customerSessionBean = lookupCustomerSessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CustomerResource
     */
    public CustomerResource() {
    }
    
    @Path("customerLogin")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response customerLogin(@QueryParam("username") String username, 
                                @QueryParam("password") String password)
    {
        try
        {
            CustomerEntity customerEntity = customerSessionBean.customerLogin(username, password);

            customerEntity.setPassword(null);
            customerEntity.setSalt(null);
            customerEntity.getOrderTransactionEntities().clear();
            customerEntity.getOrderLineItemEntities().clear();
            customerEntity.getGroupCartEntities().clear();
            
            return Response.status(Status.OK).entity(customerEntity).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerCustomer(CustomerEntity customer)
    {
        if (customer != null)
        {
            try
            {
                Long customerId = customerSessionBean.createNewCustomer(customer);
                
                return Response.status(Response.Status.OK).entity(customerId).build();
            }
            catch (CustomerUsernameExistException | UnknownPersistenceException | InputDataValidationException ex)
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
    public Response editCustomer(UpdateCustomerReq req)
    {
        if (req != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
                
                customerSessionBean.updateCustomer(req.getUpdatedCustomer());
                
                return Response.status(Response.Status.OK).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch (UpdateCustomerException | CustomerNotFoundException | InputDataValidationException ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid create new product request").build();
        }
    }
    
    @Path("isValid")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response isValid(CheckUsernameReq req)
    {
        if (req != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());

                CustomerEntity customerTemp = customerSessionBean.retrieveCustomerByUsername(req.getUsernameToCheck());
                
                Integer returnNum = customerTemp == null ? 1 : 0;
                
                return Response.status(Response.Status.OK).entity(returnNum).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch (CustomerNotFoundException ex)
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
}
