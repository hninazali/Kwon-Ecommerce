/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import entity.CreditCardEntity;

/**
 *
 * @author User
 */
public class AddNewCardReq 
{
    private String username;
    private String password;
    private CreditCardEntity creditCard;

    public AddNewCardReq(String username, String password, CreditCardEntity creditCard) {
        this.username = username;
        this.password = password;
        this.creditCard = creditCard;
    }

    public AddNewCardReq() {
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

    public CreditCardEntity getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCardEntity creditCard) {
        this.creditCard = creditCard;
    }
    
}
