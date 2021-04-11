/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.rest;

import java.util.Set;

/**
 *
 * @author User
 */
@javax.ws.rs.ApplicationPath("Resources")
public class ApplicationConfig extends javax.ws.rs.core.Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(ws.rest.BrandResource.class);
        resources.add(ws.rest.BundleResource.class);
        resources.add(ws.rest.CategoryResource.class);
        resources.add(ws.rest.CorsFilter.class);
        resources.add(ws.rest.CustomerResource.class);
        resources.add(ws.rest.GroupCartResource.class);
        resources.add(ws.rest.OrderTransactionResource.class);
        resources.add(ws.rest.PersonalCartResource.class);
        resources.add(ws.rest.ProductResource.class);
        resources.add(ws.rest.TagResource.class);
    }
    
}
