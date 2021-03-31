/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.exception;

public class PersonalCartNotFoundException extends Exception {

    public PersonalCartNotFoundException() {
    }

    public PersonalCartNotFoundException(String msg) {
        super(msg);
    }
}
