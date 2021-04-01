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
public class BundleSkuCodeExistException extends Exception {

    /**
     * Creates a new instance of <code>BundleSkuCodeExistException</code>
     * without detail message.
     */
    public BundleSkuCodeExistException() {
    }

    /**
     * Constructs an instance of <code>BundleSkuCodeExistException</code> with
     * the specified detail message.
     *
     * @param msg the detail message.
     */
    public BundleSkuCodeExistException(String msg) {
        super(msg);
    }
}
