/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.stexx.knowblog.filter;

import de.stexx.knowblog.beans.User;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.logging.Level;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Stephanie
 */
public class AuthenticationFilter implements Filter {

    private static Logger log = LoggerFactory.getLogger(AuthenticationFilter.class);
    
    @Override
    public void init(FilterConfig fc) throws ServletException {
        log.debug("init");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
        log.debug("doFilter");
        try {
            X509Certificate certs[] = (X509Certificate[])req.getAttribute("javax.servlet.request.X509Certificate");
            // ... Test if non-null, non-empty.

            if (certs != null && certs.length > 0) {
                X509Certificate clientCert = certs[0];

                // Get the Subject DN's X500Principal
                X500Principal subjectDN = clientCert.getSubjectX500Principal();

                LdapName ldapName = new LdapName(subjectDN.getName());
                String commonName = null;
                for (Rdn rdn : ldapName.getRdns()) {
                    String type = rdn.getType();
                    if (type.equalsIgnoreCase("CN")) {
                        commonName = (String) rdn.getValue();
                        break;
                    }
                }
                
                log.debug("Found certificate with subject common name: '{}'", commonName);
                
                HttpServletRequest httpReq = (HttpServletRequest) req;
                HttpSession session = httpReq.getSession(true);
                User user = (User) session.getAttribute("user");
                if (user == null) {
                    log.debug("Session object 'user' is not yet available. Creating new user with common name '{}'", commonName);
                    user = new User();
                    user.setName(commonName);
                    session.setAttribute("user", user);
                    log.debug("User '{}' stored in the session", commonName);
                } else {
                    log.debug("Session object 'user' is available.");
                    log.debug("Updating name '{}' to '{}'", user.getName(), commonName);
                    user.setName(commonName);
                }
//                // TODO: maybe use principal instead
//                HttpServletRequest httpReq = (HttpServletRequest) req;
//                httpReq.getSession(true).setAttribute("user", commonName);
            } else {
                log.debug("No client certificate found in request");
            }
        } catch (InvalidNameException ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            filterChain.doFilter(req, res);
        }
    }

    @Override
    public void destroy() {
        log.debug("destroy");
    }
    
}
