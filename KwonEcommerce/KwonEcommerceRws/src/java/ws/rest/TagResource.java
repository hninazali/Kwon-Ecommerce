/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.TagEntitySessionBeanLocal;
import entity.TagEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author User
 */
@Path("Tag")
public class TagResource 
{

    TagEntitySessionBeanLocal tagEntitySessionBean = lookupTagEntitySessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of TagResource
     */
    public TagResource() {
    }
    
    @Path("retrieveAllTags")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllTags()
    {
        try
        {
            List<TagEntity> tags = tagEntitySessionBean.retrieveAllTags();
            
            for (TagEntity tag : tags)
            {
                tag.getBundleEntities().clear();
                tag.getProductEntities().clear();
            }
            
            GenericEntity<List<TagEntity>> genericEntity = new GenericEntity<List<TagEntity>>(tags){
            };
            
            //==============================================================
            //MIGHT NEED DETACHING
            //==============================================================
            
            
            return Response.status(Response.Status.OK).entity(genericEntity).build();
        }
        catch (Exception ex)
        {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    private TagEntitySessionBeanLocal lookupTagEntitySessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (TagEntitySessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/TagEntitySessionBean!ejb.session.stateless.TagEntitySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
