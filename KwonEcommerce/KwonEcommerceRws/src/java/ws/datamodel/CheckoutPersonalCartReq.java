/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.PersonalCartEntity;

/**
 *
 * @author User
 */
public class CheckoutPersonalCartReq 
{
    private String username;
    private String password;
    private Long creditCardId;

    public CheckoutPersonalCartReq() {
    }

    public CheckoutPersonalCartReq(String username, String password, Long creditCardId) {
        this.username = username;
        this.password = password;
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

    public Long getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(Long creditCardId) {
        this.creditCardId = creditCardId;
    }
    
}
