/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf.managedbean;

import entity.BundleEntity;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.view.ViewScoped;

/**
 *
 * @author hninazali
 */
@Named(value = "viewBundleManagedbean")
@ViewScoped
public class ViewBundleManagedBean implements Serializable {

    private BundleEntity bundleEntityToView;
    /**
     * Creates a new instance of ViewBundleManagedbean
     */
    public ViewBundleManagedBean() {
        bundleEntityToView = new BundleEntity();
    }
    
    @PostConstruct
    public void postConstruct()
    {
        
    }

    /**
     * @return the bundleEntityToView
     */
    public BundleEntity getBundleEntityToView() {
        return bundleEntityToView;
    }

    /**
     * @param bundleEntityToView the bundleEntityToView to set
     */
    public void setBundleEntityToView(BundleEntity bundleEntityToView) {
        this.bundleEntityToView = bundleEntityToView;
    }
    
}
