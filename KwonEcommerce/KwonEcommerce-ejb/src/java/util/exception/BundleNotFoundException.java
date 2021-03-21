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
public class BundleNotFoundException extends Exception {

    /**
     * Creates a new instance of <code>BundleNotFoundException</code> without
     * detail message.
     */
    public BundleNotFoundException() {
    }

    /**
     * Constructs an instance of <code>BundleNotFoundException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public BundleNotFoundException(String msg) {
        super(msg);
    }
}
