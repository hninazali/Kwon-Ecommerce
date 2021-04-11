/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ws.datamodel;

import java.util.List;

/**
 *
 * @author User
 */
public class CreateGroupCartReq 
{
    private String username;
    private String password;
    private String name;
    private List<String> usernames;

    public CreateGroupCartReq() {
    }

    public CreateGroupCartReq(String username, String password, String name, List<String> usernames) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.usernames = usernames;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(List<String> usernames) {
        this.usernames = usernames;
    }
}
