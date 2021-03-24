/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author User
 */
public class OrderTransactionNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>OrderTransactionNotFoundException</code>
     * without detail message.
     */
    public OrderTransactionNotFoundException() {
    }

    /**
     * Constructs an instance of <code>OrderTransactionNotFoundException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public OrderTransactionNotFoundException(String msg) {
        super(msg);
    }
}
