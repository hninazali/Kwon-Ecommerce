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
public class DeleteStaffException extends Exception {

    /**
     * Creates a new instance of <code>DeleteStaffException</code> without
     * detail message.
     */
    public DeleteStaffException() {
    }

    /**
     * Constructs an instance of <code>DeleteStaffException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteStaffException(String msg) {
        super(msg);
    }
}
