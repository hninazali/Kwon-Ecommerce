/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.BundleEntitySessionBeanLocal;
import entity.BundleEntity;
import entity.BundleLineItemEntity;
import entity.CategoryEntity;
import entity.ProductEntity;
import entity.TagEntity;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
            for (BundleEntity bundle : bundles)
            {
                for (BundleLineItemEntity lineItem : bundle.getBundleLineItems())
                {
                    ProductEntity product = lineItem.getProductEntity();
                    if(product.getCategoryEntity().getParentCategoryEntity() != null)
                    {
                        product.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                    }

                    product.getCategoryEntity().getProductEntities().clear();
                    product.getCategoryEntity().getBundleEntities().clear();

                    for(TagEntity tagEntity:product.getTagEntities())
                    {
                        tagEntity.getProductEntities().clear();
                        tagEntity.getBundleEntities().clear();
                    }

                    product.getBrandEntity().getProductEntities().clear();
                }
                for (CategoryEntity category : bundle.getCategoryEntities())
                {
                    if (category.getParentCategoryEntity() != null)
                    {
                        category.getParentCategoryEntity().getSubCategoryEntities().clear();
                    }
                    category.getProductEntities().clear();
                    category.getBundleEntities().clear();
                }
                
                for (TagEntity tag : bundle.getTagEntities())
                {
                    tag.getProductEntities().clear();
                    tag.getBundleEntities().clear();
                }
            }
            
            GenericEntity<List<BundleEntity>> genericEntity = new GenericEntity<List<BundleEntity>>(bundles){
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
    
    @Path("retrieveBundleDetails/{bundleId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveBundleDetails(@PathParam("bundleId") Long bundleId)
    {
        try
        {
            List<BundleEntity> bundles = bundleEntitySessionBean.retrieveAllBundles();
            
            BundleEntity bundle = bundleEntitySessionBean.retrieveBundleByBundleId(bundleId);
            List<BundleLineItemEntity> lineItems = bundle.getBundleLineItems();
            
            for (BundleLineItemEntity lineItem : lineItems)
            {
                ProductEntity product = lineItem.getProductEntity();
                if(product.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    product.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                
                product.getCategoryEntity().getProductEntities().clear();
                product.getCategoryEntity().getBundleEntities().clear();
                
                for(TagEntity tagEntity:product.getTagEntities())
                {
                    tagEntity.getProductEntities().clear();
                    tagEntity.getBundleEntities().clear();
                }
                
                product.getBrandEntity().getProductEntities().clear();
            }
            
            GenericEntity<List<BundleLineItemEntity>> genericEntity = new GenericEntity<List<BundleLineItemEntity>>(lineItems){
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
