/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.service.search;

import de.stexx.knowblog.config.Configuration;
import de.stexx.knowblog.model.ContentType;
import java.util.Collections;
import java.util.List;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
public class SearchService {
    
    private static Logger log = LoggerFactory.getLogger("SearchService");
    
    public static SolrServer getSolrServer() {
        log.trace("getSolrServer");
        log.debug("Connecting to solr server {}", Configuration.getSOLRlocation());
        SolrServer solrServer = new HttpSolrServer(Configuration.getSOLRlocation(), new DefaultHttpClient(), new XMLResponseParser());
        return solrServer;
    }
    
    public static List<ContentType> search(String queryString) {
        SolrServer solrServer = SearchService.getSolrServer();
        SolrQuery query = new SolrQuery();
        query = query.setQuery(queryString);
        try {
            QueryResponse qresponse = solrServer.query(query);
            List<ContentType> docList = qresponse.getBeans(ContentType.class);
            return docList;
        } catch (SolrServerException ex) {
            log.error(ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }
    
}
