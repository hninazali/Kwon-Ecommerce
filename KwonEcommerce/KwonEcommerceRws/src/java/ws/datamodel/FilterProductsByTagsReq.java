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
public class FilterProductsByTagsReq 
{
    private String username;
    private String password;
    private List<Long> tagIds;
    private String condition;

    public FilterProductsByTagsReq() {
    }

    public FilterProductsByTagsReq(String username, String password, List<Long> tagIds, String condition) {
        this.username = username;
        this.password = password;
        this.tagIds = tagIds;
        this.condition = condition;
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

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    
}
