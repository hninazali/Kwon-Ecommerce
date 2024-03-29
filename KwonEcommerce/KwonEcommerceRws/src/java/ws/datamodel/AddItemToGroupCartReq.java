/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.GroupCartEntity;
import entity.OrderLineItemEntity;
import entity.ProductEntity;

/**
 *
 * @author User
 */
public class AddItemToGroupCartReq {
    private String username;
    private String password;
    private GroupCartEntity groupCart;
    private ProductEntity product;
    private Integer quantity;

    public AddItemToGroupCartReq() {
    }

    public AddItemToGroupCartReq(String username, String password, GroupCartEntity groupCart, ProductEntity product, Integer quantity) {
        this.username = username;
        this.password = password;
        this.groupCart = groupCart;
        this.product = product;
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

    public ProductEntity getProduct() {
        return product;
    }

    public void setProduct(ProductEntity product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    
    
}
