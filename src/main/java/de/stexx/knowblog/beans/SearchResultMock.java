package de.stexx.knowblog.beans;

import de.stexx.knowblog.model.ContentType;
import de.stexx.knowblog.service.search.SearchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
@ManagedBean
@ViewScoped
public class SearchResultMock {

    private static Logger log = LoggerFactory.getLogger("SearchResultMock");
    
    private List<ContentType> results;
    private List<Integer> pages;
    private static int RESULTS_PER_PAGE = 5;
    
//    @ManagedProperty(value = "#{param.searchQuery}")
    private String query = null;

    

    
    /**
     * Creates a new instance of SearchResultMock
     */
    public SearchResultMock() {
        
        log.debug("init");
        
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, String> parameterMap = (Map<String, String>) externalContext.getRequestParameterMap();
        query = parameterMap.get("query");
        
        log.debug("query parameter is: {}", query);
        if (query != null) {
            results = SearchService.search(query);
        } else {
            results = Collections.emptyList();
        }

        log.debug("Search results: " + results);
        
        pages = new ArrayList<Integer>();
        int page = 1;
        for (int i = 0; i < results.size(); i=i+RESULTS_PER_PAGE) {
            pages.add(page);
            page++;
        }
    }
    
    public String getQuery() {
        return query;
    }

//    public void setQuery(String query) {
//        this.query = query;
//    }

    public List<ContentType> getResults() {
        return results;
    }

    public void setResults(List<ContentType> results) {
        this.results = results;
    }

    public List<Integer> getPages() {
        return pages;
    }
    
}
