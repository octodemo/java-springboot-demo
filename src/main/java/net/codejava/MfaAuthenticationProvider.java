package net.codejava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MfaAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private MfaService mfaService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // First stage: verify username and password (primary authentication)
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        
        // Find the user entity to check MFA settings
        User user = userRepository.findByUsername(username);
        
        // Check if MFA is enabled for the user
        if (user != null && user.isMfaEnabled()) {
            // For MFA-enabled users, we return a partially authenticated token
            // The MfaAuthentication object will be used in the filter chain
            return new MfaAuthentication(
                user, 
                authentication.getCredentials(),
                userDetails.getAuthorities(),
                true  // authenticated with password, but pending MFA verification
            );
        }
        
        // For users without MFA, return a fully authenticated token
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null, // clear credentials
            userDetails.getAuthorities()
        );
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}