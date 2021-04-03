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
    private GroupCartEntity groupCart;

    public CheckoutGroupCartReq() {
    }

    public CheckoutGroupCartReq(String username, String password, GroupCartEntity groupCart) {
        this.username = username;
        this.password = password;
        this.groupCart = groupCart;
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

    public GroupCartEntity getGroupCart() {
        return groupCart;
    }

    public void setGroupCart(GroupCartEntity groupCart) {
        this.groupCart = groupCart;
    }
    
}
