/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

/**
 *
 * @author User
 */
public class CheckUsernameReq 
{
    private String username;
    private String password;
    private String usernameToCheck;

    public CheckUsernameReq(String username, String password, String usernameToCheck) {
        this.username = username;
        this.password = password;
        this.usernameToCheck = usernameToCheck;
    }

    public CheckUsernameReq() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsernameToCheck() {
        return usernameToCheck;
    }

    public void setUsernameToCheck(String usernameToCheck) {
        this.usernameToCheck = usernameToCheck;
    }
    
}
