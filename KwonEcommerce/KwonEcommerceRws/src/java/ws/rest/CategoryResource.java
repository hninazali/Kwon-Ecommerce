/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CategoryEntitySessionBeanLocal;
import entity.CategoryEntity;
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
@Path("Category")
public class CategoryResource 
{

    CategoryEntitySessionBeanLocal categoryEntitySessionBean = lookupCategoryEntitySessionBeanLocal();
    

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of CategoryResource
     */
    public CategoryResource() {
    }
    
    @Path("retrieveAllCategories")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveAllCategories()
    {
        try
        {
            List<CategoryEntity> categories = categoryEntitySessionBean.retrieveAllCategories();
            
            for (CategoryEntity category : categories)
            {
                category.getBundleEntities().clear();
                category.getProductEntities().clear();
                category.getSubCategoryEntities().clear();
            }
            
            GenericEntity<List<CategoryEntity>> genericEntity = new GenericEntity<List<CategoryEntity>>(categories){
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

    private CategoryEntitySessionBeanLocal lookupCategoryEntitySessionBeanLocal() {
        try {
            javax.naming.Context c = new InitialContext();
            return (CategoryEntitySessionBeanLocal) c.lookup("java:global/KwonEcommerce/KwonEcommerce-ejb/CategoryEntitySessionBean!ejb.session.stateless.CategoryEntitySessionBeanLocal");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
