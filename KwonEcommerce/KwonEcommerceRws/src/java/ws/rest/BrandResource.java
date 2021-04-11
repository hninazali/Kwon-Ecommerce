/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.BrandEntitySessionBeanLocal;
import entity.BrandEntity;
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

/**
 * REST Web Service
 *
 * @author User
 */
@Path("Brand")
public class BrandResource 
{

    BrandEntitySessionBeanLocal brandEntitySessionBean = lookupBrandEntitySessionBeanLocal();

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of BrandResource
     */
    public BrandResource() {
    }
    
    @Path("retrieveAllBrands")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllBrands()
    {
        List<BrandEntity> brands = brandEntitySessionBean.retrieveAllBrands();
        for (BrandEntity brand : brands)
        {
            brand.getProductEntities().clear();
        }
        
        GenericEntity<List<BrandEntity>> genericEntity = new GenericEntity<List<BrandEntity>>(brands){
        };
        
        return Response.status(Response.Status.OK).entity(genericEntity).build();
    }

    private BrandEntitySessionBeanLocal lookupBrandEntitySessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (BrandEntitySessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/BrandEntitySessionBean!ejb.session.stateless.BrandEntitySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}