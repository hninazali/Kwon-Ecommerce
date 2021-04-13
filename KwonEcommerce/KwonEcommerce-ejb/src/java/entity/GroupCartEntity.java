/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class GroupCartEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupCartId;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(max = 32)
    private String name;
    @ManyToOne
    @JoinColumn(nullable = false)
    private CustomerEntity groupOwner;

    @ManyToMany
    private List<CustomerEntity> customerEntities;

    @OneToMany
    private List<OrderLineItemEntity> orderLineItemEntities;

    public GroupCartEntity() {
        customerEntities = new ArrayList<>();
        orderLineItemEntities = new ArrayList<>();
    }

    public GroupCartEntity(String name, CustomerEntity groupOwner) {
        this.name = name;
        this.groupOwner = groupOwner;
    }

    public Long getGroupCartId() {
        return groupCartId;
    }

    public void setGroupCartId(Long groupCartId) {
        this.groupCartId = groupCartId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (groupCartId != null ? groupCartId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the groupCartId fields are not set
        if (!(object instanceof GroupCartEntity)) {
            return false;
        }
        GroupCartEntity other = (GroupCartEntity) object;
        if ((this.groupCartId == null && other.groupCartId != null) || (this.groupCartId != null && !this.groupCartId.equals(other.groupCartId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.GroupCartEntity[ id=" + groupCartId + " ]";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CustomerEntity> getCustomerEntities() {
        return customerEntities;
    }

    public void setCustomerEntities(List<CustomerEntity> customerEntities) {
        this.customerEntities = customerEntities;
    }

    public List<OrderLineItemEntity> getOrderLineItemEntities() {
        return orderLineItemEntities;
    }

    public void setOrderLineItemEntities(List<OrderLineItemEntity> orderLineItemEntities) {
        this.orderLineItemEntities = orderLineItemEntities;
    }

    public CustomerEntity getGroupOwner() {
        return groupOwner;
    }

    public void setGroupOwner(CustomerEntity groupOwner) {
        this.groupOwner = groupOwner;
    }
}
