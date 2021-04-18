/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.security.CryptographicHelper;

@Entity
public class CustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long customerId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String firstName;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String lastName;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(max = 32)
    private String username;
    @Column(nullable = false, unique = true, length = 64)
    @NotNull
    @Size(max = 64)
    @Email
    private String email;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 8, max = 32)
    private String password;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 8, max = 100)
    private String address;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 6, max = 10)
    private String postalCode;
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    private String salt;
    @Column(nullable = false)
    @NotNull
    private Boolean banned;
    @OneToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(nullable = false)
    private PersonalCartEntity personalCartEntity;

    @OneToMany(mappedBy = "customerEntity", fetch = FetchType.LAZY)
    private List<OrderLineItemEntity> orderLineItemEntities;

    @ManyToMany(mappedBy = "customerEntities", fetch = FetchType.LAZY)
    private List<GroupCartEntity> groupCartEntities;

    @OneToMany(mappedBy = "customerEntity", fetch = FetchType.LAZY)
    private List<OrderTransactionEntity> orderTransactionEntities;

    @OneToMany(fetch = FetchType.LAZY)
    private List<CreditCardEntity> creditCardEntities;

    public CustomerEntity() {
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);
        personalCartEntity = new PersonalCartEntity();
        orderLineItemEntities = new ArrayList<>();
        orderTransactionEntities = new ArrayList<>();
        groupCartEntities = new ArrayList<>();
        creditCardEntities = new ArrayList<>();
    }

    public CustomerEntity(String firstName, String lastName, String email, String password, PersonalCartEntity personalCartEntity, Boolean banned) {
        this();

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.personalCartEntity = personalCartEntity;
        this.banned = false;

        setPassword(password);
    }
    
    public CustomerEntity(String firstName, String lastName, String username, String email, String password, PersonalCartEntity personalCartEntity, Boolean banned) {
        this();

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.personalCartEntity = personalCartEntity;
        this.banned = false;

        setPassword(password);
    }
    
    public CustomerEntity(String firstName, String lastName, String username, String email, String password, String address, String postalCode, PersonalCartEntity personalCartEntity, Boolean banned) {
        this();

        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.address = address;
        this.postalCode = postalCode;
        this.personalCartEntity = personalCartEntity;
        this.banned = false;

        setPassword(password);
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.customerId != null ? this.customerId.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CustomerEntity)) {
            return false;
        }

        CustomerEntity other = (CustomerEntity) object;

        if ((this.customerId == null && other.customerId != null) || (this.customerId != null && !this.customerId.equals(other.customerId))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "entity.CustomerEntity[ customerId=" + this.customerId + " ]";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null) {
            this.password = CryptographicHelper.getInstance().byteArrayToHexString(CryptographicHelper.getInstance().doMD5Hashing(password + this.salt));
        } else {
            this.password = null;
        }
    }
    
    public void setPasswordCustom(String password)
    {
        this.password = password;
    }

    public PersonalCartEntity getPersonalCartEntity() {
        return personalCartEntity;
    }

    public void setPersonalCartEntity(PersonalCartEntity personalCartEntity) {
        this.personalCartEntity = personalCartEntity;
    }

    public List<OrderLineItemEntity> getOrderLineItemEntities() {
        return orderLineItemEntities;
    }

    public void setOrderLineItemEntities(List<OrderLineItemEntity> orderLineItemEntities) {
        this.orderLineItemEntities = orderLineItemEntities;
    }

    public List<GroupCartEntity> getGroupCartEntities() {
        return groupCartEntities;
    }

    public void setGroupCartEntities(List<GroupCartEntity> groupCartEntities) {
        this.groupCartEntities = groupCartEntities;
    }

    public List<OrderTransactionEntity> getOrderTransactionEntities() {
        return orderTransactionEntities;
    }

    public void setOrderTransactionEntities(List<OrderTransactionEntity> orderTransactionEntities) {
        this.orderTransactionEntities = orderTransactionEntities;
    }

    public List<CreditCardEntity> getCreditCardEntities() {
        return creditCardEntities;
    }

    public void setCreditCardEntities(List<CreditCardEntity> creditCardEntities) {
        this.creditCardEntities = creditCardEntities;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
