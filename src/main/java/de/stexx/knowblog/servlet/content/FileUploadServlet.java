/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.servlet.content;

import de.stexx.knowblog.beans.User;
import de.stexx.knowblog.config.Configuration;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
public class FileUploadServlet extends HttpServlet {
 
    private int maxMemSize = 4096;
    private int maxFileSize = 4096;
    private String filePath = null;
    private static Logger log = LoggerFactory.getLogger(FileUploadServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        filePath = Configuration.getImagePath();
    }
    
    @Override
    public void doPost(HttpServletRequest request, 
               HttpServletResponse response)
              throws ServletException, java.io.IOException {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null || user.getName() == null) {
            response.setStatus(403);
        }
        log.debug("doPost");
        // Check that we have a file upload request
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        response.setContentType("text/html");
        if( !isMultipart ){
           throw new ServletException("Upload is not a multipart content type");
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File("c:\\temp"));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax( maxFileSize );

        try{ 
            // Parse the request to get file items.
            List<FileItem> fileItems = upload.parseRequest(request);
            // Process the uploaded file items
            Iterator<FileItem> i = fileItems.iterator();

            while ( i.hasNext () ) 
            {
               FileItem fi = i.next();
               if ( !fi.isFormField () )	
               {
                  // Get the uploaded file parameters
                  String fieldName = fi.getFieldName();
                  String fileName = fi.getName();
                  String contentType = fi.getContentType();
                  boolean isInMemory = fi.isInMemory();
                  long sizeInBytes = fi.getSize();
                  File file = null;
                  
                  String tmpFilePath = this.filePath + user.getName() + File.pathSeparator + "upload"  + File.pathSeparator;
                  // Write the file
                  if( fileName.lastIndexOf(File.pathSeparator) >= 0 ){
                     file = new File(tmpFilePath + 
                     fileName.substring(fileName.lastIndexOf(File.pathSeparator))) ;
                  } else{
                     file = new File( tmpFilePath + 
                     fileName.substring(fileName.lastIndexOf(File.pathSeparator)+1)) ;
                  }
                  fi.write(file) ;
                  log.debug("Uploaded Filename: '{}' to file '{}'", fileName, file.getAbsoluteFile());
               }
            }
         }catch(Exception ex) {
             System.out.println(ex);
         }
    }
}
