/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.BundleEntity;

/**
 *
 * @author User
 */
public class AddBundleToPersonalCartReq 
{
    private String username;
    private String password;
    private BundleEntity bundle;
    private Integer quantity;

    public AddBundleToPersonalCartReq(String username, String password, BundleEntity bundle, Integer quantity) {
        this.username = username;
        this.password = password;
        this.bundle = bundle;
        this.quantity = quantity;
    }

    public AddBundleToPersonalCartReq() {
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
