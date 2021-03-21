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
public class UpdateBundleException extends Exception {

    /**
     * Creates a new instance of <code>UpdateBundleException</code> without
     * detail message.
     */
    public UpdateBundleException() {
    }

    /**
     * Constructs an instance of <code>UpdateBundleException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public UpdateBundleException(String msg) {
        super(msg);
    }
}
