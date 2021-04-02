/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class PersonalCartEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long personalCartId;

//    @OneToOne(mappedBy = "personalCartEntity")
//    @JoinColumn(nullable = false)
//    private CustomerEntity customerEntity;

    @OneToMany
    private List<OrderLineItemEntity> orderLineItemEntities;

    public PersonalCartEntity() {
        orderLineItemEntities = new ArrayList<>();
    }

//    public PersonalCartEntity(CustomerEntity customerEntity) {
//        this.customerEntity = customerEntity;
//    }
    
    public Long getPersonalCartId() {
        return personalCartId;
    }

    public void setPersonalCartId(Long personalCartId) {
        this.personalCartId = personalCartId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (personalCartId != null ? personalCartId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the personalCartId fields are not set
        if (!(object instanceof PersonalCartEntity)) {
            return false;
        }
        PersonalCartEntity other = (PersonalCartEntity) object;
        if ((this.personalCartId == null && other.personalCartId != null) || (this.personalCartId != null && !this.personalCartId.equals(other.personalCartId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.PersonalCartEntity[ id=" + personalCartId + " ]";
    }

//    public CustomerEntity getCustomerEntity() {
//        return customerEntity;
//    }
//
//    public void setCustomerEntity(CustomerEntity customerEntity) {
//        this.customerEntity = customerEntity;
//    }

    public List<OrderLineItemEntity> getOrderLineItemEntities() {
        return orderLineItemEntities;
    }

    public void setOrderLineItemEntities(List<OrderLineItemEntity> orderLineItemEntities) {
        this.orderLineItemEntities = orderLineItemEntities;
    }

}
