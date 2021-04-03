/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 *
 * @author hninazali
 */
@Entity
public class BundleEntity implements Serializable {

    /**
     * @return the bundleRating
     */
    public Integer getBundleRating() {
        return bundleRating;
    }

    /**
     * @param bundleRating the bundleRating to set
     */
    public void setBundleRating(Integer bundleRating) {
        this.bundleRating = bundleRating;
    }

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bundleId;
    
    @NotNull
    @Size(min = 7, max = 7)
    private String skuCode;
    
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(max = 64)
    private String name;
    
    @Column(length = 128)
    @Size(max = 128)
    private String description;
    
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer quantityOnHand;
    
    @Column(nullable = false, precision = 11, scale = 2)
    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 9, fraction = 2) // 11 - 2 digits to the left of the decimal point
    private BigDecimal unitPrice;  
    
     @Column(nullable = false)
    @NotNull
    @Positive
    @Min(1)
    @Max(5)
    private Integer bundleRating;
  
    @ManyToMany    
    private List<ProductEntity> productEntities;
    
    @ManyToMany(mappedBy="bundleEntities", fetch = FetchType.LAZY)
    private List<CategoryEntity> categoryEntities;
    
    @ManyToMany(mappedBy = "bundleEntities", fetch = FetchType.LAZY)
    private List<TagEntity> tagEntities;
    
    

    public BundleEntity() {
    }
    
    
    
    public BundleEntity(String bundleSkuCode, String name, String description, BigDecimal unitPrice, List<ProductEntity> productEntities) {
        this.skuCode = bundleSkuCode;
        this.name = name;
        this.description = description;
        this.unitPrice = unitPrice;
        this.productEntities = productEntities;
        
        for(ProductEntity product : productEntities){
            categoryEntities.add(product.getCategoryEntity());
        }
        
    }
    
    
    public void addTag(TagEntity tagEntity)
    {
        if(tagEntity != null)
        {
            if(!this.tagEntities.contains(tagEntity))
            {
                this.tagEntities.add(tagEntity);
                
                if(!tagEntity.getBundleEntities().contains(this))
                {                    
                    tagEntity.getBundleEntities().add(this);
                }
            }
        }
    }
    
    
    
    public void removeTag(TagEntity tagEntity)
    {
        if(tagEntity != null)
        {
            if(this.tagEntities.contains(tagEntity))
            {
                this.tagEntities.remove(tagEntity);
                
                if(tagEntity.getProductEntities().contains(this))
                {
                    tagEntity.getProductEntities().remove(this);
                }
            }
        }
    }
    
     public void addProduct(ProductEntity productEntity)
    {
        if(productEntity != null)
        {
            if(!this.productEntities.contains(productEntity))
            {
                this.productEntities.add(productEntity);
                this.categoryEntities.add(productEntity.getCategoryEntity());
                
//                if(!productEntity.getBundleEntities().contains(this))
//                {                    
//                    productEntity.getBundleEntities().add(this);
//                }
            }
        }
    }
     
    //might not need to use this method, because everytime there is an update, you just clear the entire list and add back.
     public void removeProduct(ProductEntity productEntity)
    {
        if(productEntity != null)
        {
            if(this.productEntities.contains(productEntity))
            {
                this.productEntities.remove(productEntity);
                
//                if(productEntity.getBundleEntities().contains(this))
//                {
//                    productEntity.getBundleEntities().remove(this);
//                }
            }
        }
    }
    

    public Long getBundleId() {
        return bundleId;
    }

    public void setBundleId(Long bundleId) {
        this.bundleId = bundleId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (bundleId != null ? bundleId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the bundleId fields are not set
        if (!(object instanceof BundleEntity)) {
            return false;
        }
        BundleEntity other = (BundleEntity) object;
        if ((this.bundleId == null && other.bundleId != null) || (this.bundleId != null && !this.bundleId.equals(other.bundleId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.BundleEntity[ id=" + bundleId + " ]";
    }

    /**
     * @return the skuCode
     */
    public String getSkuCode() {
        return skuCode;
    }

    /**
     * @param skuCode the skuCode to set
     */
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the unitPrice
     */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /**
     * @param unitPrice the unitPrice to set
     */
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * @return the productEntities
     */
    public List<ProductEntity> getProductEntities() {
        return productEntities;
    }

    /**
     * @param productEntities the productEntities to set
     */
    public void setProductEntities(List<ProductEntity> productEntities) {
        this.productEntities = productEntities;
    }

    /**
     * @return the categoryEntities
     */
    public List<CategoryEntity> getCategoryEntities() {
        return categoryEntities;
    }

    /**
     * @return the tagEntities
     */
    public List<TagEntity> getTagEntities() {
        return tagEntities;
    }

    /**
     * @param tagEntities the tagEntities to set
     */
    public void setTagEntities(List<TagEntity> tagEntities) {
        this.tagEntities = tagEntities;
    }

    /**
     * @return the quantityOnHand
     */
    public Integer getQuantityOnHand() {
        return quantityOnHand;
    }

    /**
     * @param quantityOnHand the quantityOnHand to set
     */
    public void setQuantityOnHand(Integer quantityOnHand) {
        this.quantityOnHand = quantityOnHand;
    }

  
    
}
