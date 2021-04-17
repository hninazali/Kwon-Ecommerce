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
public class TooMuchQuantityException extends Exception {

    /**
     * Creates a new instance of <code>TooMuchQuantityException</code> without
     * detail message.
     */
    public TooMuchQuantityException() {
    }

    /**
     * Constructs an instance of <code>TooMuchQuantityException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public TooMuchQuantityException(String msg) {
        super(msg);
    }
}
