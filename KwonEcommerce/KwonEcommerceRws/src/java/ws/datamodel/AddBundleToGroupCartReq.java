/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.GroupCartEntity;
import entity.BundleEntity;

/**
 *
 * @author User
 */
public class AddBundleToGroupCartReq 
{
    private String username;
    private String password;
    private GroupCartEntity groupCart;
    private BundleEntity bundle;
    private Integer quantity;

    public AddBundleToGroupCartReq() {
    }

    public AddBundleToGroupCartReq(String username, String password, GroupCartEntity groupCart, BundleEntity bundle, Integer quantity) {
        this.username = username;
        this.password = password;
        this.groupCart = groupCart;
        this.bundle = bundle;
        this.quantity = quantity;
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

    public BundleEntity getBundle() {
        return bundle;
    }

    public void setBundle(BundleEntity bundle) {
        this.bundle = bundle;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
}
