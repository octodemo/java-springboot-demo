package net.codejava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class MfaAuthenticationFilter extends OncePerRequestFilter {

    private static final String MFA_VERIFICATION_URL = "/mfa/verify";
    private static final String MFA_SETUP_URL = "/mfa/setup";
    private static final String LOGIN_URL = "/login";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpSession session = request.getSession();
        
        // Skip the filter for login and MFA-related URLs
        String requestURI = request.getRequestURI();
        if (requestURI.equals(LOGIN_URL) || requestURI.startsWith(MFA_VERIFICATION_URL) || 
            requestURI.startsWith(MFA_SETUP_URL) || requestURI.startsWith("/css/") || 
            requestURI.startsWith("/js/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Check if the user is authenticated with MFA
        if (authentication instanceof MfaAuthentication) {
            MfaAuthentication mfaAuthentication = (MfaAuthentication) authentication;
            
            // If MFA is needed but not yet verified
            if (mfaAuthentication.getUser().isMfaEnabled() && !mfaAuthentication.isMfaAuthenticated()) {
                // Store the requested URL in the session
                session.setAttribute("REQUESTED_URL", requestURI);
                
                // Redirect to MFA verification page
                response.sendRedirect(MFA_VERIFICATION_URL);
                return;
            }
        }
        
        filterChain.doFilter(request, response);
    }
}