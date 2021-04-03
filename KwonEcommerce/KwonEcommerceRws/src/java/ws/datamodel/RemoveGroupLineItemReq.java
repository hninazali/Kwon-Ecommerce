/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.GroupCartEntity;
import entity.OrderLineItemEntity;

/**
 *
 * @author User
 */
public class RemoveGroupLineItemReq 
{
    private String username;
    private String password;
    private OrderLineItemEntity lineItem;
    private GroupCartEntity groupCart;

    public RemoveGroupLineItemReq() {
    }

    public RemoveGroupLineItemReq(String username, String password, OrderLineItemEntity lineItem, GroupCartEntity groupCart) {
        this.username = username;
        this.password = password;
        this.lineItem = lineItem;
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

    public OrderLineItemEntity getLineItem() {
        return lineItem;
    }

    public void setLineItem(OrderLineItemEntity lineItem) {
        this.lineItem = lineItem;
    }

    public GroupCartEntity getGroupCart() {
        return groupCart;
    }

    public void setGroupCart(GroupCartEntity groupCart) {
        this.groupCart = groupCart;
    }
    
    
}
