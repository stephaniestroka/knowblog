package de.stexx.knowblog.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

/**
 *
 * @author Stephanie
 */
public class Configuration {

    private static Properties config;
    private static Configuration instance = null;

    private Logger log = LoggerFactory.getLogger("Configuration");
    
    private Configuration() throws Exception {
        String pathConfig = System.getProperty("knowblog.config");
        if (pathConfig == null) {
            throw new Exception("Path to configuration has to be specified with the system property 'knowblog.config'");
        }
        config = new Properties();
        log.debug("Looking for configuration parameters in file '" + pathConfig + "'");
        File configFile = new File(pathConfig);
        if (!configFile.exists()) {
            throw new FileNotFoundException("The file '" + pathConfig + "' does not exist");
        }
        if (!configFile.canRead()) {
            throw new IOException("Cannot read file '" + pathConfig + "'");
        }
        FileInputStream configFileStream = new FileInputStream(configFile);
        StringBuilder sb = new StringBuilder();
        while (configFileStream.available() > 0) {
            byte[] b = new byte[1];
            int status = configFileStream.read(b);
            if (status == -1) {
                throw new IOException("Error while reading from config file '" + pathConfig + "'");
            }
            byte backslash = (byte) '\\';
            if (b[0] == backslash) {
                sb.append("\\\\");
            } else {
                sb.append(new String(b, "UTF-8"));
            }
        }
        StringReader reader = new StringReader(sb.toString());
        config.load(reader);
    }
    
    private static void precondition() {
        if (instance == null) {
            synchronized (Configuration.class) {
                try {
                    instance = new Configuration();
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
    }
    
    private static String getFullyQualifiedPath(String path) {
        if (!path.substring(path.length()-1).equals(File.separator)) {
            return path + File.separator;
        }
        return path;
    }
    
    public static String getHTMLDocumentPath() {
        precondition();
        String htmlDocumentPath = config.getProperty("html.document.path");
        return getFullyQualifiedPath(htmlDocumentPath);
    }
    
    public static String getImagePath() {
        precondition();
        return getFullyQualifiedPath(config.getProperty("image.path"));
    }
    
    public static String getSOLRlocation() {
        precondition();
        return config.getProperty("solr.location");
    }
}
