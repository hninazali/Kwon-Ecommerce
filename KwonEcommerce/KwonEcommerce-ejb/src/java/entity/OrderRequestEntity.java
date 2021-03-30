/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 *
 * @author User
 */
@Entity
public class OrderRequestEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderRequestId;
    @Column(nullable = false)
    @NotNull
    private boolean isApproved;
    @Column(nullable = false)
    @NotNull
    private Integer quantityOrdered;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ProductEntity product;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private SupplierEntity supplier;
    
    public OrderRequestEntity()
    {
        
    }

    public OrderRequestEntity(boolean isApproved, Integer quantityOrdered) {
        this.isApproved = isApproved;
        this.quantityOrdered = quantityOrdered;
    }
    
    

    public Long getOrderRequestId() {
        return orderRequestId;
    }

    public void setOrderRequestId(Long orderRequestId) {
        this.orderRequestId = orderRequestId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderRequestId != null ? orderRequestId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the orderRequestId fields are not set
        if (!(object instanceof OrderRequestEntity)) {
            return false;
        }
        OrderRequestEntity other = (OrderRequestEntity) object;
        if ((this.orderRequestId == null && other.orderRequestId != null) || (this.orderRequestId != null && !this.orderRequestId.equals(other.orderRequestId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OrderRequestEntity[ id=" + orderRequestId + " ]";
    }

    public boolean isIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public SupplierEntity getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierEntity supplier) {
        this.supplier = supplier;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public Integer getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(Integer quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }
    
}
