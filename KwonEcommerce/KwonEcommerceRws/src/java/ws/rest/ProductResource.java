/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ProductEntitySessionBeanLocal;
import entity.CategoryEntity;
import entity.CustomerEntity;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import util.exception.BrandNotFoundException;
import util.exception.CategoryNotFoundException;
import util.exception.CustomerNotFoundException;
import util.exception.InvalidLoginCredentialException;
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

    CustomerSessionBeanLocal customerSessionBean = lookupCustomerSessionBeanLocal();

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
            List<ProductEntity> productEntities1 = productEntitySessionBean.filterProductsByBrand(brandId);
            ArrayList<ProductEntity> productEntities = new ArrayList<>();
            productEntities.addAll(productEntities1);
            ProductEntity productEntity = new ProductEntity();
            Integer temp = productEntities.size();
            //System.out.println("TESTING   " + products.get(1).getName());
            List<ProductEntity> products = new ArrayList<>();
            
            for(int i = 0; i < temp; i++)
            {
                productEntity = productEntities.get(i);
                productEntity.getTagEntities().clear();
                productEntity.setBrandEntity(null);
                if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                
                productEntity.getCategoryEntity().getProductEntities().clear();
                productEntity.getCategoryEntity().getBundleEntities().clear();
                products.add(productEntity);
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
    
    @Path("getRecommendations")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRecommendations(@QueryParam("username") String username, 
                                        @QueryParam("password") String password)
    {
        try
        {
            CustomerEntity customer = customerSessionBean.customerLogin(username, password);
            
            List<ProductEntity> productEntities1 = productEntitySessionBean.retrieveRecommendations(customer.getCustomerId());
            
            ArrayList<ProductEntity> productEntities = new ArrayList<>();
            productEntities.addAll(productEntities1);
            ProductEntity productEntity = new ProductEntity();
            Integer temp = productEntities.size();
            List<ProductEntity> products = new ArrayList<>();
            
            for(int i = 0; i < temp; i++)
            {
                productEntity = productEntities.get(i);
                if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                productEntity.getCategoryEntity().getProductEntities().clear();
                productEntity.getCategoryEntity().getBundleEntities().clear();
                products.add(productEntity);
                productEntity.getBrandEntity().getProductEntities().clear();
                for(TagEntity tagEntity:productEntity.getTagEntities())
                {
                    tagEntity.getProductEntities().clear();
                    tagEntity.getBundleEntities().clear();
                }
            }
            
            GenericEntity<List<ProductEntity>> genericEntity = new GenericEntity<List<ProductEntity>>(products){
            };
            
            return Response.status(Status.OK).entity(genericEntity).build();
        }
        catch(InvalidLoginCredentialException ex)
        {
            return Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).build();
        }
        catch(CustomerNotFoundException | CategoryNotFoundException ex)
        {
            return Response.status(Response.Status.BAD_REQUEST).entity(ex.getMessage()).build();
        }
    }
    
    @Path("filterProductsByCategory/{categoryId}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response filterProductsByCategory(@PathParam("categoryId") Long categoryId)
    {
        try
        {
            List<ProductEntity> productEntities1 = productEntitySessionBean.filterProductsByCategory(categoryId);
            ArrayList<ProductEntity> productEntities = new ArrayList<>();
            productEntities.addAll(productEntities1);
            //System.out.println("++++++++++++++   " + productEntities.size());
            ProductEntity productEntity = new ProductEntity();
            Integer temp = productEntities.size();
            //System.out.println("TESTING   " + products.get(1).getName());
            List<ProductEntity> products = new ArrayList<>();
            
            for(int i = 0; i < temp; i++)
            {
                //System.out.println("AAAAAAAAAA");
                productEntity = productEntities.get(i);
                //System.out.println(productEntity.getName() + "   11111111");
                if(productEntity.getCategoryEntity().getParentCategoryEntity() != null)
                {
                    productEntity.getCategoryEntity().getParentCategoryEntity().getSubCategoryEntities().clear();
                }
                //System.out.println("!!!!!!!!!ADDED!!!!!");
                productEntity.getCategoryEntity().getProductEntities().clear();
                productEntity.getCategoryEntity().getBundleEntities().clear();
                products.add(productEntity);
                //System.out.println("ADDED!!!!!");
                productEntity.getBrandEntity().getProductEntities().clear();
                for(TagEntity tagEntity:productEntity.getTagEntities())
                {
                    tagEntity.getProductEntities().clear();
                    tagEntity.getBundleEntities().clear();
                }
//                productEntity.getBrandEntity().getBundleEntities().clear();
            }
            //System.out.println("$$$$$$$   " + productEntities.size());
            
            GenericEntity<List<ProductEntity>> genericEntity = new GenericEntity<List<ProductEntity>>(products){
            };
            
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
