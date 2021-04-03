/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.BundleEntitySessionBeanLocal;
import entity.BundleEntity;
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
@Path("Bundle")
public class BundleResource 
{

    BundleEntitySessionBeanLocal bundleEntitySessionBean = lookupBundleEntitySessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of BundleResource
     */
    public BundleResource() {
    }
    
    @Path("retrieveAllBundles")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllBundles()
    {
        try
        {
            List<BundleEntity> bundles = bundleEntitySessionBean.retrieveAllBundles();
            
            GenericEntity<List<BundleEntity>> genericEntity = new GenericEntity<List<BundleEntity>>(bundles){
            };
            
            return Response.status(Response.Status.OK).entity(genericEntity).build();
        }
        catch (Exception ex)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    private BundleEntitySessionBeanLocal lookupBundleEntitySessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (BundleEntitySessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/BundleEntitySessionBean!ejb.session.stateless.BundleEntitySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
