package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;



@Entity

public class TagEntity implements Serializable 
{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(max = 32)
    private String name;
    
    @ManyToMany(fetch = FetchType.LAZY)
    private List<ProductEntity> productEntities;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<BundleEntity> bundleEntities;
    

    
    public TagEntity() 
    {
        productEntities = new ArrayList<>();
        bundleEntities = new ArrayList<>();
    }

    
    
    public TagEntity(String name) 
    {
        this();
        
        this.name = name;
    }
    
    
    
    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tagId != null ? tagId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the tagId fields are not set
        if (!(object instanceof TagEntity)) {
            return false;
        }
        TagEntity other = (TagEntity) object;
        if ((this.tagId == null && other.tagId != null) || (this.tagId != null && !this.tagId.equals(other.tagId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.TagEntity[ id=" + tagId + " ]";
    }    

    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductEntity> getProductEntities() {
        return productEntities;
    }

    public void setProductEntities(List<ProductEntity> productEntities) {
        this.productEntities = productEntities;
    }

    public List<BundleEntity> getBundleEntities() {
        return bundleEntities;
    }

    public void setBundleEntities(List<BundleEntity> bundleEntities) {
        this.bundleEntities = bundleEntities;
    }
}