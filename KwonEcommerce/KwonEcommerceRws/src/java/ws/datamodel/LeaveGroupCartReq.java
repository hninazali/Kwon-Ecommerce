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
public class LeaveGroupCartReq 
{
    private String username;
    private String password;
    private Long groupCartId;

    public LeaveGroupCartReq() {
    }

    public LeaveGroupCartReq(String username, String password, Long groupCartId) {
        this.username = username;
        this.password = password;
        this.groupCartId = groupCartId;
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

    public Long getGroupCartId() {
        return groupCartId;
    }

    public void setGroupCartId(Long groupCartId) {
        this.groupCartId = groupCartId;
    }
    
}
