/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

public class CustomerExistInCartException extends Exception {

    public CustomerExistInCartException() {
    }

    public CustomerExistInCartException(String msg) {
        super(msg);
    }
}
