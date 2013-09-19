package de.stexx.knowblog.beans;

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author Stephanie
 */
@ManagedBean
@RequestScoped
public class CategoryMock {

    private List<String> categories;
    /**
     * Creates a new instance of CategoryMock
     */
    public CategoryMock() {
        categories = new ArrayList<String>();
        categories.add("Traveling");
        categories.add("Books");
        categories.add("Programming");
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }
}
