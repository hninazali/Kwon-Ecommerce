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
public class AccountUsernameExistException extends Exception {

    /**
     * Creates a new instance of <code>AccountUsernameExistException</code>
     * without detail message.
     */
    public AccountUsernameExistException() {
    }

    /**
     * Constructs an instance of <code>AccountUsernameExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public AccountUsernameExistException(String msg) {
        super(msg);
    }
}
