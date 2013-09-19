/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.model;

import java.util.Date;
import org.apache.solr.client.solrj.beans.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
public class ContentType {
    
    @Field("title")
    private String title;
    @Field("content")
    private String content;
    @Field("created")
    private Date created;
    @Field("last_modified")
    private Date lastModified;
    @Field("id")
    private String id;
    @Field("previous_id")
    private String previous;
    @Field("next_id")
    private String next;

    private static int SUMMARY_LENGTH = 450;

    private static Logger log = LoggerFactory.getLogger("SearchResultMock");
    
    /*public ContentType(String title) {
        this.title = title;
        this.id = Base64.encodeBase64String(title.getBytes());
    }*/
    
    public ContentType() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }
    
    public String getSummary() {
        if (content == null) {
            log.error("Found inconsistence in contentType {}", title);
            content = "";
        }
        if (content.length() > SUMMARY_LENGTH) {
            return content.substring(0, SUMMARY_LENGTH) + "...";
        }
        return content;
    }
    
    public String toString() {
        return "ContentType:" + this.getTitle();
    }
}
