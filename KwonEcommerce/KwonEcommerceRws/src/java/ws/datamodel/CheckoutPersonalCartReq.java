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
    private PersonalCartEntity personalCart;

    public CheckoutPersonalCartReq() {
    }

    public CheckoutPersonalCartReq(String username, String password, PersonalCartEntity personalCart) {
        this.username = username;
        this.password = password;
        this.personalCart = personalCart;
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

    public PersonalCartEntity getPersonalCart() {
        return personalCart;
    }

    public void setPersonalCart(PersonalCartEntity personalCart) {
        this.personalCart = personalCart;
    }
    
}
