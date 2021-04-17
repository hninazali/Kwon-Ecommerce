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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.AccessRightEnum;
import util.security.CryptographicHelper;

@Entity
public class StaffEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String firstName;
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(max = 32)
    private String lastName;
//    @Column(nullable = false, unique = true, length = 32)
//    @NotNull
//    @Size(max = 32)
//    private String fullName;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AccessRightEnum accessRightEnum;
    @Column(nullable = false, unique = true, length = 32)
    @NotNull
    @Size(min = 4, max = 32)
    private String username;
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    @NotNull
    private String password;
    @Column(columnDefinition = "CHAR(32) NOT NULL")
    private String salt;

    @OneToMany(mappedBy = "staffEntity", fetch = FetchType.LAZY)
    private List<OrderTransactionEntity> orderTransactionEntities;
    
    public StaffEntity() {
        this.salt = CryptographicHelper.getInstance().generateRandomString(32);

        orderTransactionEntities = new ArrayList<>();
    }

    public StaffEntity(String firstName, String lastName, AccessRightEnum accessRightEnum, String username, String password) {
        this();

        this.firstName = firstName;
        this.lastName = lastName;
        this.accessRightEnum = accessRightEnum;
        this.username = username;
//        this.fullName = firstName + " " + lastName;
        
        //this.password = password;

        setPassword(password);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.staffId != null ? this.staffId.hashCode() : 0);

        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof StaffEntity)) {
            return false;
        }

        StaffEntity other = (StaffEntity) object;

        if ((this.staffId == null && other.staffId != null) || (this.staffId != null && !this.staffId.equals(other.staffId))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "entity.StaffEntity[ staffId=" + this.staffId + " ]";
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
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

    public AccessRightEnum getAccessRightEnum() {
        return accessRightEnum;
    }

    public void setAccessRightEnum(AccessRightEnum accessRightEnum) {
        this.accessRightEnum = accessRightEnum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public List<OrderTransactionEntity> getOrderTransactionEntities() {
        return orderTransactionEntities;
    }

    public void setOrderTransactionEntities(List<OrderTransactionEntity> orderTransactionEntities) {
        this.orderTransactionEntities = orderTransactionEntities;
    }

//    /**
//     * @return the fullName
//     */
//    public String getFullName() {
//        return fullName;
//    }
//
//    /**
//     * @param fullName the fullName to set
//     */
//    public void setFullName(String fullName) {
//        this.fullName = fullName;
//    }
}
