/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.CustomerEntity;

/**
 *
 * @author User
 */
public class UpdateCustomerReq 
{
    private String username;
    private String password;
    private CustomerEntity updatedCustomer;

    public UpdateCustomerReq() {
    }

    public UpdateCustomerReq(String username, String password, CustomerEntity updatedCustomer) {
        this.username = username;
        this.password = password;
        this.updatedCustomer = updatedCustomer;
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

    public CustomerEntity getUpdatedCustomer() {
        return updatedCustomer;
    }

    public void setUpdatedCustomer(CustomerEntity updatedCustomer) {
        this.updatedCustomer = updatedCustomer;
    }
    
    
}
