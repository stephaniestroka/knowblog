package de.stexx.knowblog.servlet.content;

import de.stexx.knowblog.beans.User;
import de.stexx.knowblog.model.ContentType;
import de.stexx.knowblog.service.content.BrowseContentService;
import de.stexx.knowblog.service.content.ContentNotFoundException;
import de.stexx.knowblog.service.content.ContentTypeFactory;
import de.stexx.knowblog.service.content.CreateContentService;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
public class CreateContentServlet extends HttpServlet {

    private static Logger log = LoggerFactory.getLogger("CreateContentServlet");
    
    @Override
    public void init() throws ServletException {
        super.init();
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("CreateContentServlet", "doPost");
        
        HttpSession session = req.getSession();
        if (session == null) {
            resp.sendError(403);
            return;
        }
        User user = (User) session.getAttribute("user");
        if (user == null || user.getName() == null) {
            resp.sendError(403);
            return;
        }
        
        String title = req.getParameter("title");
        String content = (String) req.getAttribute("filteredContent");
        
        log.debug("Creating new content with title {}, content {}", title, content);
        
        ContentType contentType = ContentTypeFactory.createContentType(title);
        contentType.setContent(content);
        contentType.setCreated(new Date());
        try {
            try {
                ContentType previousContent = BrowseContentService.getLatestContent();
                log.debug("Setting previous field of content with title {} to content with title {}", contentType.getTitle(), previousContent.getTitle());
                contentType.setPrevious(previousContent.getId());
                log.debug("Setting next field of content with title {} to content with title {}", previousContent.getTitle(), contentType.getTitle());
                previousContent.setNext(contentType.getId());
                CreateContentService.create(previousContent);
            } catch (ContentNotFoundException ex) {
                log.error(ex.getMessage(), ex);
            }
            log.debug("Persisting new content {}", contentType);
            CreateContentService.create(contentType);
            resp.sendRedirect("/knowblog/");
        } catch (FileAlreadyExistsException e) {
            resp.setStatus(500);
        } catch (IOException e) {
            resp.setStatus(500);
        } catch (SolrServerException ex) {
            System.out.println(ex.getMessage());
            resp.setStatus(500);
        }
    }
}
