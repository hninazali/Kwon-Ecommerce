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
public class CreateNewBundleException extends Exception {

    /**
     * Creates a new instance of <code>CreateNewBundleException</code> without
     * detail message.
     */
    public CreateNewBundleException() {
    }

    /**
     * Constructs an instance of <code>CreateNewBundleException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public CreateNewBundleException(String msg) {
        super(msg);
    }
}
