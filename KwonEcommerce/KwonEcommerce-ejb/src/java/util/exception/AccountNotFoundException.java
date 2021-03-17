/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author winyfebriny
 */
public class AccountNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>AccountNotFoundException</code> without
     * detail message.
     */
    public AccountNotFoundException() {
    }

    /**
     * Constructs an instance of <code>AccountNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
