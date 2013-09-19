package de.stexx.knowblog.service.content;

import de.stexx.knowblog.config.Configuration;
import de.stexx.knowblog.model.ContentType;
import de.stexx.knowblog.service.search.SearchService;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
public class CreateContentService {

    private static Logger log = LoggerFactory.getLogger("CreateContentService");
    
    public static void create(ContentType contentType) throws FileAlreadyExistsException, IOException, SolrServerException {
        log.debug("Indexing content {}", contentType);
        try {
            SolrServer server = SearchService.getSolrServer();
            server.deleteById(contentType.getId());
            server.addBean(contentType);
            server.commit();
            log.debug("Committed changes in solr server");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
