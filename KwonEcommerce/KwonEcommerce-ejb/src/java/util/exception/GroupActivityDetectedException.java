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
public class GroupActivityDetectedException extends Exception {

    /**
     * Creates a new instance of <code>GroupActivityDetectedException</code>
     * without detail message.
     */
    public GroupActivityDetectedException() {
    }

    /**
     * Constructs an instance of <code>GroupActivityDetectedException</code>
     * with the specified detail message.
     *
     * @param msg the detail message.
     */
    public GroupActivityDetectedException(String msg) {
        super(msg);
    }
}
