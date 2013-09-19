package de.stexx.knowblog.beans;

import de.stexx.knowblog.model.ContentType;
import de.stexx.knowblog.service.content.BrowseContentService;
import de.stexx.knowblog.service.content.ContentNotFoundException;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
@ManagedBean
@ViewScoped
public class ContentBean {

    private ContentType contentType;
    
       
    private static Logger log = LoggerFactory.getLogger("ContentBean");
    
    /**
     * Creates a new instance of ContentMock
     */
    public ContentBean() {
        try {
            log.debug("Searching for the latest content...");
            contentType = BrowseContentService.getLatestContent();
        } catch (ContentNotFoundException ex) {
            log.info(ex.getMessage(), ex);
            contentType = new ContentType();
        }
    }

    public String getTitle() {
        return contentType.getTitle();
    }

    public String getContent() {
        return contentType.getContent();
    }

    public Date getCreated() {
        return contentType.getCreated();
    }
    
    
    public void displayPrevious() {
        try {
            log.debug("Retrieving previous content");
            contentType = BrowseContentService.getPrevious(contentType);
        } catch (ContentNotFoundException ex) {
            log.info(ex.getMessage(), ex);
        }
    }
    
    public void displayNext() {
        try {
            log.debug("Retrieving next content of current content {}", contentType.getTitle());
            contentType = BrowseContentService.getNext(contentType);
        } catch (ContentNotFoundException ex) {
            log.info(ex.getMessage(), ex);
        }
    }
    
    public boolean hasPrevious() {
        log.debug("ContentBean : hasPrevious");
        boolean ret = false;
        try {
            return ret = contentType.getPrevious() != null;
        } finally {
            log.debug("hasPrevious({})", ret);
        }
    }
    
    public boolean hasNext() {
        log.debug("ContentBean : hasNext");
        boolean ret = false;
        try {
            return ret = contentType.getNext() != null;
        } finally {
            log.debug("hasNext({})", ret);
        }
    }
}
