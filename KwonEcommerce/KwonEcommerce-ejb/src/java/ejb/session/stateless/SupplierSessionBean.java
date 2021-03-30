/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.SupplierEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author User
 */
@Stateless
public class SupplierSessionBean implements SupplierSessionBeanLocal {

    @PersistenceContext(unitName = "KwonEcommerce-ejbPU")
    private EntityManager em;
    
    public SupplierSessionBean()
    {   
    }
    
    @Override
    public List<SupplierEntity> retrieveAllSuppliers()
    {
        Query query = em.createQuery("SELECT sp FROM SupplierEntity sp");
        
        return query.getResultList();
    }
}
