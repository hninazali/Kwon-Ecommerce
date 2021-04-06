/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.BundleLineItemEntity;
import javax.ejb.Local;
import util.exception.BundleNotFoundException;

/**
 *
 * @author User
 */
@Local
public interface BundleLineItemEntitySessionBeanLocal {

    public BundleLineItemEntity retrieveBundleLineItemByBundleLineItemId(Long bundleLineItemId) throws BundleNotFoundException;
    
}
