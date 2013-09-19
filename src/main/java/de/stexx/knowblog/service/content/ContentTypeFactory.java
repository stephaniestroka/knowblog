/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.service.content;

import de.stexx.knowblog.model.ContentType;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Stephanie
 */
public class ContentTypeFactory {
    
    public static ContentType createContentType(String title) {
        ContentType contentType = new ContentType();
        contentType.setTitle(title);
        contentType.setId(Base64.encodeBase64String(title.getBytes()));
        return contentType;
    }
    
//    public static ContentType addContentToContentType(ContentType contentType) throws FileNotFoundException, IOException {
//        String documentPath = Configuration.getHTMLDocumentPath();
//        String base64title = Base64.encodeBase64String(contentType.getTitle().getBytes());
//        File contentFile = new File(documentPath + base64title);
//        if (!contentFile.canRead()) {
//            throw new RuntimeException("Cannot read content file '" + contentFile.getAbsolutePath() + "'");
//        }
//        StringBuilder sb = new StringBuilder();
//        FileInputStream fin = new FileInputStream(contentFile);
//        while (fin.available() > 0) {
//            int amount = fin.available() > 256 ? 256 : fin.available();
//            byte[] buffer = new byte[amount];
//            fin.read(buffer);
//            sb.append(new String(buffer, "UTF-8"));
//        }
//        contentType.setContent(sb.toString());
//        return contentType;
//    }
}
