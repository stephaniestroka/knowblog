/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.filter;

import de.stexx.knowblog.exception.ContentNotAllowedException;
import de.stexx.knowblog.config.Configuration;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author Stephanie
 */
public class CreateContentFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(CreateContentFilter.class);
    
    private static String filePath = null;
    private static List<String> allowedElements = null;
    private static List<String> allowedAttributes = null;
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
        log.debug("init");
        filePath = Configuration.getImagePath();
        allowedElements = new ArrayList<String>();
        allowedElements.add("root");
        allowedElements.add("img");
        allowedElements.add("br");
        allowedElements.add("b");
        allowedElements.add("i");
        allowedElements.add("h2");
        allowedElements.add("h3");
        allowedElements.add("h4");
        allowedElements.add("h5");
        allowedElements.add("ul");
        allowedElements.add("li");
        allowedElements.add("h5");
        allowedElements.add("pre");
        allowedAttributes = new ArrayList<String>();
        allowedAttributes.add("src");
        allowedAttributes.add("alt");
        allowedAttributes.add("height");
        allowedAttributes.add("width");
    }
    
    private String XMLtoString(Node n) throws TransformerConfigurationException, TransformerException {
        StringWriter writer = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(n), new StreamResult(writer));
        return writer.toString();
    }
    
    
    private void replaceBrackets(Element e) throws TransformerConfigurationException, TransformerException {
        NodeList preChildren = e.getChildNodes();
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < preChildren.getLength(); j++) {
            Node preChild = preChildren.item(j);
            String xml = XMLtoString(preChild);
            sb.append(xml);
            e.removeChild(preChild);
        }
        String preText = sb.toString();
        preText = preText.replaceAll("<", "&lt;");
        preText = preText.replaceAll(">", "&gt;");
        Document document = e.getOwnerDocument();
        e.appendChild(document.createTextNode(preText));
    }

    private void replaceSrcImage(Element e, String title) throws ContentNotAllowedException {
        if (!e.getTagName().equalsIgnoreCase("img")) {
            throw new ContentNotAllowedException("Attribute 'src' is only allowed in 'img' Element");
        }
        Attr attr = e.getAttributeNode("img");
        String value = attr.getValue();
        String actualPath = value;
        if (value.startsWith("/knowblog/images/")) {
            actualPath = "/knowblog/images/" + Base64.encodeBase64String(title.getBytes()) + "/" + value.substring("/knowblog/images/".length());
        }
        attr.setValue(actualPath);
    }
    
    private void scan(Node document, String title) throws TransformerConfigurationException, TransformerException, ContentNotAllowedException {
        log.debug("Scanning document '{}'", document.getNodeName());
        if (document.hasChildNodes()) {
            NodeList nl = document.getChildNodes();
            for (int i=0; i<nl.getLength(); i++) {
                Node n = nl.item(i);
                scan(n, title);
                if (n instanceof Element) {
                    Element e = (Element) n;
                    String tagName = e.getTagName();
                    if (allowedElements.contains(tagName.toLowerCase())) {
                        log.debug("Found allowed element '{}'", tagName);
                        if (tagName.equalsIgnoreCase("pre")) {
                            replaceBrackets(e);
                        }
                        NamedNodeMap attributes = e.getAttributes();
                        for (int j = 0; j < attributes.getLength(); j++) {
                            Attr attribute = (Attr) attributes.item(j);
                            String attributeName = attribute.getName();
                            if (!allowedAttributes.contains(attributeName.toLowerCase())) {
                                throw new ContentNotAllowedException("Content with HTML element attribute '" + attributeName + "' not allowed");
                            }
                            log.debug("Found allowed attribute '{}'", attributeName);
                            if (attributeName.equalsIgnoreCase("src")) {
                                replaceSrcImage(e, title);
                            }
                        }
                    } else {
                        throw new ContentNotAllowedException("Content with HTML element '" + tagName + "' not allowed");
                    }
                }
            }
        }
    }
    
    @Override
    public void doFilter(ServletRequest sreq, ServletResponse sres, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) sreq;
        HttpServletResponse res = (HttpServletResponse) sres;
        
        String title = req.getParameter("title");
        if (title == null || title.isEmpty()) {
            log.error("Missing title");
            res.sendError(500, "Request is incomplete");
            return;
        }
        String content = req.getParameter("content");
        if (content == null || content.isEmpty()) {
            log.error("Missing content");
            res.sendError(500, "Request is incomplete");
            return;
        }
        
        DocumentBuilder docBuilder;
        try {
            docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.parse(new InputSource(new StringReader("<root>"+content+"</root>")));
            
            scan(document, title);
            String xml = XMLtoString(document);
            
            xml = xml.substring(xml.indexOf('>') + 1);
            xml = xml.replace("<root>", "");
            xml = xml.replace("</root>", "");
            
            sreq.setAttribute("filteredContent", xml);
            
            filterChain.doFilter(sreq, sres);
        } catch (ParserConfigurationException ex) {
            log.error(ex.getMessage(), ex);
        } catch (SAXException ex) {
            log.error(ex.getMessage(), ex);
        } catch (TransformerConfigurationException ex) {
            log.error(ex.getMessage(), ex);
        } catch (TransformerException ex) {
            log.error(ex.getMessage(), ex);
        } catch (ContentNotAllowedException e) {
            log.error(e.getMessage(), e);
            res.sendError(403, e.getMessage());
        }
    }

    @Override
    public void destroy() {
    }
    
}
