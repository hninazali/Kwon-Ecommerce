/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.OrderLineItemEntity;
import entity.ProductEntity;

/**
 *
 * @author User
 */
public class AddItemToPersonalCartReq 
{
    private String username;
    private String password;
    private ProductEntity product;
    private Integer quantity;
    
    public AddItemToPersonalCartReq()
    {
        
    }

    public AddItemToPersonalCartReq(String username, String password, ProductEntity product, Integer quantity) {
        this.username = username;
        this.password = password;
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
