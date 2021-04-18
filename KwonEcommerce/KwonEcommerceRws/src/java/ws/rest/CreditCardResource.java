/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CreditCardSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import entity.CreditCardEntity;
import entity.CustomerEntity;
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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import util.exception.CreateNewCreditCardException;
import util.exception.CreditCardNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import ws.datamodel.AddNewCardReq;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("CreditCard")
public class CreditCardResource 
{

    CreditCardSessionBeanLocal creditCardSessionBean = lookupCreditCardSessionBeanLocal();

    CustomerSessionBeanLocal customerSessionBean = lookupCustomerSessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CreditCardResource
     */
    public CreditCardResource() {
    }
    
    @Path("retrieveAllCreditCards")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllCreditCards(@QueryParam("username") String username,
                                            @QueryParam("password") String password)
    {
        if (username != null)
        {
            try 
            {
                CustomerEntity customer = customerSessionBean.customerLogin(username, password);
                
                List<CreditCardEntity> creditCards = customer.getCreditCardEntities();
                
                GenericEntity<List<CreditCardEntity>> genericEntity = new GenericEntity<List<CreditCardEntity>>(creditCards){
                };
                
                return Response.status(Response.Status.OK).entity(genericEntity).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid retrieve all credit cards request").build();
        }
    }
    
    @Path("addNewCard")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addNewCreditCard(AddNewCardReq req)
    {
        if (req != null)
        {
            try
            {
                CustomerEntity customer = customerSessionBean.customerLogin(req.getUsername(), req.getPassword());
                
                CreditCardEntity creditCard = creditCardSessionBean.createCreditCard(customer.getCustomerId(), req.getCreditCard());

                return Response.status(Response.Status.OK).entity(creditCard.getCreditCardId()).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(InputDataValidationException | CreateNewCreditCardException ex)
            {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid add new credit card request").build();
        }
    }
    
    @Path("{creditCardId}")
    @DELETE
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCreditCard(@QueryParam("username") String username, 
                                        @QueryParam("password") String password,
                                        @PathParam("creditCardId") Long creditCardId)
    {
        if (creditCardId != null)
        {
            try
            {

                CustomerEntity customer = customerSessionBean.customerLogin(username, password);

                creditCardSessionBean.deleteCreditCard(customer.getCustomerId(), creditCardId);

                return Response.status(Response.Status.OK).build();
            }
            catch(InvalidLoginCredentialException ex)
            {
                return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
            }
            catch(CreditCardNotFoundException | CustomerNotFoundException  ex)
            {
                return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
            }
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid Delete Credit Card request").build();
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

    private CreditCardSessionBeanLocal lookupCreditCardSessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CreditCardSessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/CreditCardSessionBean!ejb.session.stateless.CreditCardSessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
