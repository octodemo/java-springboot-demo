package net.codejava;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MfaAuthentication extends UsernamePasswordAuthenticationToken {
    
    private boolean mfaAuthenticated;
    private User user;

    public MfaAuthentication(User user, Object credentials, 
                            Collection<? extends GrantedAuthority> authorities,
                            boolean mfaAuthenticated) {
        super(user.getUsername(), credentials, authorities);
        this.user = user;
        this.mfaAuthenticated = mfaAuthenticated;
    }

    public boolean isMfaAuthenticated() {
        return mfaAuthenticated;
    }

    public void setMfaAuthenticated(boolean mfaAuthenticated) {
        this.mfaAuthenticated = mfaAuthenticated;
    }
    
    public User getUser() {
        return user;
    }

    @Override
    public boolean isAuthenticated() {
        // Only consider the authentication fully complete if MFA has been verified
        // for users who have MFA enabled
        if (user.isMfaEnabled()) {
            return super.isAuthenticated() && mfaAuthenticated;
        }
        return super.isAuthenticated();
    }
}