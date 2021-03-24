/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author User
 */
@Entity
public class ProductOrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productOrderId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    @NotNull
    private Date requestDateTime;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ProductEntity productEntity;
    @ManyToOne
    @JoinColumn(nullable = false)
    private SupplierEntity supplier;

    public ProductOrderEntity() {
    }

    public ProductOrderEntity(Date requestDateTime, ProductEntity productEntity, SupplierEntity supplier) {
        this.requestDateTime = requestDateTime;
        this.productEntity = productEntity;
        this.supplier = supplier;
    }

    public Long getProductOrderId() {
        return productOrderId;
    }

    public void setProductOrderId(Long productOrderId) {
        this.productOrderId = productOrderId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productOrderId != null ? productOrderId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the productOrderId fields are not set
        if (!(object instanceof ProductOrderEntity)) {
            return false;
        }
        ProductOrderEntity other = (ProductOrderEntity) object;
        if ((this.productOrderId == null && other.productOrderId != null) || (this.productOrderId != null && !this.productOrderId.equals(other.productOrderId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ProductOrderEntity[ id=" + productOrderId + " ]";
    }

    public Date getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(Date requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }

    public SupplierEntity getSupplier() {
        return supplier;
    }

    public void setSupplier(SupplierEntity supplier) {
        this.supplier = supplier;
    }
    
}
