/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.service.content;

import de.stexx.knowblog.model.ContentType;
import de.stexx.knowblog.service.search.SearchService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
public class BrowseContentService {
    
    private static Logger log = LoggerFactory.getLogger("BrowseContentService");
    
    private static ContentType getContentTypeById(String id) throws ContentNotFoundException {
        List<ContentType> docList = SearchService.search("id:" + id);
        // TODO check that there is only 1
        if (docList.size() != 1) {
            throw new ContentNotFoundException("Could not find content with id '" + id + "'");
        }
        ContentType item = docList.get(0);
//            item = ContentTypeFactory.addContentToContentType(item);
        return item;
    }
    
    public static ContentType getPrevious(ContentType contentType) throws ContentNotFoundException {
        log.trace("BrowseContentService: getPrevious({})", contentType);
        ContentType previousContent = null;
        try {
            String previousId = contentType.getPrevious();
            log.debug("ID of previous content: {}", previousId);
            if (previousId == null) {
                return previousContent = ContentTypeFactory.createContentType("404 - Not Found");
            } else {
                return previousContent = getContentTypeById(previousId);    
            }
        } finally {
            log.debug("Retrieving content with title {}", previousContent == null ? null : previousContent.getTitle());
        }
    }
    
    public static ContentType getNext(ContentType contentType) throws ContentNotFoundException {
        log.trace("BrowseContentService: getNext({})", contentType);
        ContentType nextContent = null;
        try {
            String nextId = contentType.getNext();
            log.debug("ID of next content: {}", nextId);
            if (nextId == null) {
                return nextContent = ContentTypeFactory.createContentType("404 - Not Found");
            } else {
                return nextContent = getContentTypeById(nextId);    
            }
        } finally {
            log.debug("Retrieving content with title {}", nextContent == null ? null : nextContent.getTitle());
        }
    }

    public static boolean hasPrevious(ContentType contentType) throws ContentNotFoundException {
        String idOfPrevious = contentType.getPrevious();
        if (idOfPrevious == null) {
            return false;
        }
        return true;
    }

    public static boolean hasNext(ContentType contentType) throws ContentNotFoundException {
        String idOfPrevious = contentType.getPrevious();
        if (idOfPrevious == null) {
            return false;
        }
        return true;
    }

    public static ContentType getLatestContent() throws ContentNotFoundException {
        SolrServer solrServer = SearchService.getSolrServer();
        SolrQuery query = new SolrQuery();
        query = query.setQuery("*:*").setRows(1).addSort("created", SolrQuery.ORDER.desc);
        try {
            QueryResponse qresponse = solrServer.query(query);
            List<ContentType> docList = qresponse.getBeans(ContentType.class);
            // TODO check that there is only 1
            ContentType item = null;
            if (docList.size() > 1) {
                item = docList.get(0);
            } else {
                item = ContentTypeFactory.createContentType("");
            }
            return item;
        } catch (SolrServerException ex) {
            log.error(ex.getMessage(), ex);
        }
        throw new ContentNotFoundException("No content found");
    }
}
