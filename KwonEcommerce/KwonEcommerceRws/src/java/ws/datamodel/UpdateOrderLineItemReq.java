/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.OrderLineItemEntity;

/**
 *
 * @author User
 */
public class UpdateOrderLineItemReq 
{
    private String username;
    private String password;
    
    private OrderLineItemEntity lineItem;
    private Integer newQty;

    public UpdateOrderLineItemReq() {
    }

    public UpdateOrderLineItemReq(String username, String password, OrderLineItemEntity lineItem, Integer newQty) {
        this.username = username;
        this.password = password;
        this.lineItem = lineItem;
        this.newQty = newQty;
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

    public OrderLineItemEntity getLineItem() {
        return lineItem;
    }

    public void setLineItem(OrderLineItemEntity lineItem) {
        this.lineItem = lineItem;
    }

    public Integer getNewQty() {
        return newQty;
    }

    public void setNewQty(Integer newQty) {
        this.newQty = newQty;
    }
    
    
    
}
