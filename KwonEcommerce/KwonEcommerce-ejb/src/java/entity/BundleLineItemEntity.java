package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity

@NamedQueries({
    @NamedQuery(name = "selectAllOrderLineItemsByProductId", query = "SELECT stli FROM OrderLineItemEntity stli WHERE stli.productEntity.productId = :inProductId")
})

public class BundleLineItemEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bundleLineItemId;
//    @Column(nullable = false)
//    @NotNull
//    @Min(1)
//    private Integer serialNumber;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ProductEntity productEntity;
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer quantity;
//    @Column(nullable = false, precision = 11, scale = 2)
//    @NotNull
//    @DecimalMin("0.00")
//    @Digits(integer = 9, fraction = 2) // 11 - 2 digits to the left of the decimal point
//    private BigDecimal unitPrice;
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2) // 11 - 2 digits to the left of the decimal point
    private BigDecimal subTotal;

    public BundleLineItemEntity() {
    }

//    public BundleLineItemEntity(Integer serialNumber, ProductEntity productEntity, Integer quantity, BigDecimal unitPrice, BigDecimal subTotal) {
//        this.serialNumber = serialNumber;
//        this.productEntity = productEntity;
//        this.quantity = quantity;
//        this.unitPrice = unitPrice;
//        this.subTotal = subTotal;

    public BundleLineItemEntity(ProductEntity productEntity, Integer quantity, BigDecimal subTotal) 
    {
        //this.serialNumber = serialNumber;
        this.productEntity = productEntity;
        this.quantity = quantity;
        this.subTotal = subTotal;
    }

//    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.bundleLineItemId != null ? this.bundleLineItemId.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof BundleLineItemEntity)) {
            return false;
        }

        BundleLineItemEntity other = (BundleLineItemEntity) object;

        if ((this.bundleLineItemId == null && other.bundleLineItemId != null) || (this.bundleLineItemId != null && !this.bundleLineItemId.equals(other.bundleLineItemId))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "entity.BundleLineItemEntity[ bundleLineItemId=" + this.bundleLineItemId + " ]";
    }

    public Long getBundleLineItemId() {
        return bundleLineItemId;
    }

    public void setBundleLineItemId(Long bundleLineItemId) {
        this.bundleLineItemId = bundleLineItemId;
    }

//    public Integer getSerialNumber() {
//        return serialNumber;
//    }
//
//    public void setSerialNumber(Integer serialNumber) {
//        this.serialNumber = serialNumber;
//    }

    public ProductEntity getProductEntity() {
        return productEntity;
    }

    public void setProductEntity(ProductEntity productEntity) {
        this.productEntity = productEntity;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

//    public BigDecimal getUnitPrice() {
//        return unitPrice;
//    }
//
//    public void setUnitPrice(BigDecimal unitPrice) {
//        this.unitPrice = unitPrice;
//    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
