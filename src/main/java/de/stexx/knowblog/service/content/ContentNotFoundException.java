package de.stexx.knowblog.service.content;

/**
 *
 * @author Stephanie
 */
public class ContentNotFoundException extends Exception {

    public ContentNotFoundException() {
    }

    public ContentNotFoundException(Throwable cause) {
        super(cause);
    }
    
    public ContentNotFoundException(String message) {
        super(message);
    }
}
