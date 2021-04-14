/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.ProductEntitySessionBeanLocal;
import entity.ProductEntity;
import entity.TagEntity;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
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
import javax.ws.rs.core.Response.Status;
import util.exception.BrandNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.ProductNotFoundException;
import ws.datamodel.FilterProductsByTagsReq;

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
            
            for(ProductEntity productEntity : products)
            {
                if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                
                productEntity.getCategoryEntity().getProductEntities().clear();
                productEntity.getCategoryEntity().getBundleEntities().clear();
                
                for(TagEntity tagEntity:productEntity.getTagEntities())
                {
                    tagEntity.getProductEntities().clear();
                    tagEntity.getBundleEntities().clear();
                }
                
                productEntity.getBrandEntity().getProductEntities().clear();
                //productEntity.getBrandEntity().getBundleEntities().clear();
            }
            
            GenericEntity<List<ProductEntity>> genericEntity = new GenericEntity<List<ProductEntity>>(products){
            };
            
            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch (Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("filterProductsByBrand/{brandId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProductsByBrand(@PathParam("brandId") Long brandId)
    {
        try
        {
            List<ProductEntity> products = productEntitySessionBean.filterProductsByBrand(brandId);
            
            for(ProductEntity productEntity : products)
            {
                productEntity.getTagEntities().clear();
                productEntity.setBrandEntity(null);
                if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                
                productEntity.getCategoryEntity().getProductEntities().clear();
                productEntity.getCategoryEntity().getBundleEntities().clear();
//                
//                for(TagEntity tagEntity:productEntity.getTagEntities())
//                {
//                    tagEntity.getProductEntities().clear();
//                    tagEntity.getBundleEntities().clear();
//                }
//                
//                productEntity.getBrandEntity().getProductEntities().clear();
                //productEntity.getBrandEntity().getBundleEntities().clear();
            }
            
            GenericEntity<List<ProductEntity>> genericEntity = new GenericEntity<List<ProductEntity>>(products){
            };
            
            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch (BrandNotFoundException ex)
        {
            return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    @Path("filterProductsByCategory/{categoryId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProductsByCategory(@PathParam("categoryId") Long categoryId)
    {
        try
        {
            List<ProductEntity> productEntities = productEntitySessionBean.filterProductsByCategory(categoryId);
            //System.out.println("++++++++++++++   " + products.size());
            ProductEntity productEntity = new ProductEntity();
            //System.out.println("TESTING   " + products.get(1).getName());
            List<ProductEntity> products = new ArrayList<>();
            
            for(int i = 0; i < productEntities.size(); i++)
            {
                System.out.println("AAAAAAAAAA");
                productEntity = productEntities.get(i);
                //System.out.println(productEntity.getName() + "  BBBBBBBBBBBBBB");
                //productEntity.setCategoryEntity(null);
                productEntity.getTagEntities().clear();
                productEntity.setBrandEntity(null);
                System.out.println("ADDED!!!!!+++++++++++");
                if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                System.out.println("!!!!!!!!!ADDED!!!!!");
                //productEntity.getCategoryEntity().getProductEntities().clear();
                //productEntity.getCategoryEntity().getBundleEntities().clear();
                products.add(productEntity);
                System.out.println("ADDED!!!!!");
//                productEntity.getCategoryEntity().setParentCategoryEntity(null);
//                productEntity.getCategoryEntity().getSubCategoryEntities().clear();
//                productEntity.getCategoryEntity().getProductEntities().clear();
//                productEntity.getCategoryEntity().getBundleEntities().clear();
//                productEntity.setCategoryEntity(null);
//                    productEntity.getBrandEntity().getProductEntities().clear();
//                    for(TagEntity tagEntity:productEntity.getTagEntities())
//                    {
//                        tagEntity.getProductEntities().clear();
//                        tagEntity.getBundleEntities().clear();
//                    }
//                productEntity.getBrandEntity().getBundleEntities().clear();
            }
            
            GenericEntity<List<ProductEntity>> genericEntity = new GenericEntity<List<ProductEntity>>(products){
            };
            
            System.out.println("REACHABLE JUGA");
            
            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch (CategoryNotFoundException ex)
        {
            return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    @Path("filterProductsByTag/{tagId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProductsByTag(@PathParam("tagId") Long tagId)
    {
        try
        {
            List<ProductEntity> products = productEntitySessionBean.filterProductsByTag(tagId);
            
            for(ProductEntity productEntity : products)
            {
                //productEntity.setCategoryEntity(null);
                productEntity.getTagEntities().clear();
                productEntity.setBrandEntity(null);
                if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                
                productEntity.getCategoryEntity().getProductEntities().clear();
                productEntity.getCategoryEntity().getBundleEntities().clear();
//                
//                for(TagEntity tagEntity:productEntity.getTagEntities())
//                {
//                    tagEntity.getProductEntities().clear();
//                    tagEntity.getBundleEntities().clear();
//                }
//                
//                productEntity.getBrandEntity().getProductEntities().clear();
                //productEntity.getBrandEntity().getBundleEntities().clear();
            }
            
            GenericEntity<List<ProductEntity>> genericEntity = new GenericEntity<List<ProductEntity>>(products){
            };
            
            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch (Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("retrieveProduct/{productId}")
    @GET
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response retrieveProduct(@PathParam("productId") Long productId)
    {
        try
        {
            ProductEntity productEntity = productEntitySessionBean.retrieveProductByProductId(productId);
            
            if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
            {
                productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
            }

            productEntity.getCategoryEntity().getProductEntities().clear();
            productEntity.getCategoryEntity().getBundleEntities().clear();

            for(TagEntity tagEntity:productEntity.getTagEntities())
            {
                tagEntity.getProductEntities().clear();
                tagEntity.getBundleEntities().clear();
            }
            
            productEntity.getBrandEntity().getProductEntities().clear();
            
            return Response.status(Status.OK).entity(productEntity).build();
        }
        catch(ProductNotFoundException ex)
        {
            return Response.status(Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
        catch(Exception ex)
        {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }
    
    @Path("filterProductsByTags")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProductsByTags(FilterProductsByTagsReq req)
    {
        try
        {
            List<ProductEntity> products = productEntitySessionBean.filterProductsByTags(req.getTagIds(), req.getCondition());
            
            for(ProductEntity productEntity : products)
            {
                if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                
                productEntity.getCategoryEntity().getProductEntities().clear();
                productEntity.getCategoryEntity().getBundleEntities().clear();
                
                for(TagEntity tagEntity:productEntity.getTagEntities())
                {
                    tagEntity.getProductEntities().clear();
                    tagEntity.getBundleEntities().clear();
                }
                
                productEntity.getBrandEntity().getProductEntities().clear();
                //productEntity.getBrandEntity().getBundleEntities().clear();
            }
            
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
