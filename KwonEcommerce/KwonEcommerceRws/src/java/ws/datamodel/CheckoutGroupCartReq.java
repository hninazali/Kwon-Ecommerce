/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.GroupCartEntity;

/**
 *
 * @author User
 */
public class CheckoutGroupCartReq 
{
    private String username;
    private String password;
    private Long groupCartId;
    private Long creditCardId;

    public CheckoutGroupCartReq() {
    }

    public CheckoutGroupCartReq(String username, String password, Long groupCartId, Long creditCardId) {
        this.username = username;
        this.password = password;
        this.groupCartId = groupCartId;
        this.creditCardId = creditCardId;
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
        this.password = password;
    }

    public Long getGroupCartId() {
        return groupCartId;
    }

    public void setGroupCartId(Long groupCartId) {
        this.groupCartId = groupCartId;
    }

    public Long getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(Long creditCardId) {
        this.creditCardId = creditCardId;
    }
    
}
