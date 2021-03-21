/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

/**
 *
 * @author hninazali
 */
public class DeleteBundleException extends Exception {

    /**
     * Creates a new instance of <code>DeleteBundleException</code> without
     * detail message.
     */
    public DeleteBundleException() {
    }

    /**
     * Constructs an instance of <code>DeleteBundleException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DeleteBundleException(String msg) {
        super(msg);
    }
}
