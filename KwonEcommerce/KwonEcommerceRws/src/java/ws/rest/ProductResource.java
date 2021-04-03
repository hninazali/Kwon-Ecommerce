/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ProductEntitySessionBeanLocal;
import entity.ProductEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("Product")
public class ProductResource 
{

    ProductEntitySessionBeanLocal productEntitySessionBean = lookupProductEntitySessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ProductResource
     */
    public ProductResource() {
    }

    @Path("retrieveAllProducts")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllProducts()
    {
        try
        {
            List<ProductEntity> products = productEntitySessionBean.retrieveAllProducts();
            
            GenericEntity<List<ProductEntity>> genericEntity = new GenericEntity<List<ProductEntity>>(products){
            };
            
            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch (Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    private ProductEntitySessionBeanLocal lookupProductEntitySessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (ProductEntitySessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/ProductEntitySessionBean!ejb.session.stateless.ProductEntitySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
