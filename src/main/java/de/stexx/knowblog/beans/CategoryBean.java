/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.beans;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author Stephanie
 */
@ManagedBean
@ViewScoped
public class CategoryBean {

    private List<String> categories;
    
    public CategoryBean() {
        
        categories = new ArrayList<String>();
        categories.add("Travelling");
        categories.add("Programming");
        categories.add("Photography");
        categories.add("Books");
    }
    
    public List<String> getCategories() {
        return categories;
    }
}
