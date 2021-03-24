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
public class OrderTransactionAlreadyVoidedRefundedException extends Exception {

    /**
     * Creates a new instance of
     * <code>OrderTransactionAlreadyVoidedRefundedException</code> without
     * detail message.
     */
    public OrderTransactionAlreadyVoidedRefundedException() {
    }

    /**
     * Constructs an instance of
     * <code>OrderTransactionAlreadyVoidedRefundedException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public OrderTransactionAlreadyVoidedRefundedException(String msg) {
        super(msg);
    }
}
