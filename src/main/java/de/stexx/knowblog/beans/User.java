/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.beans;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Stephanie
 */
@ManagedBean
@SessionScoped
public class User {

    private String name = null;
    
    /**
     * Creates a new instance of User
     */
    public User() {
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}
