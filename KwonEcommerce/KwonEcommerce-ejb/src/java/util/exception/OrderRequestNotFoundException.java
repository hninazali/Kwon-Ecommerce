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
public class OrderRequestNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>OrderRequestNotFoundException</code>
     * without detail message.
     */
    public OrderRequestNotFoundException() {
    }

    /**
     * Constructs an instance of <code>OrderRequestNotFoundException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public OrderRequestNotFoundException(String msg) {
        super(msg);
    }
}
