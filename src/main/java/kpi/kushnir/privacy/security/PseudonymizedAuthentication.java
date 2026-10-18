package kpi.kushnir.privacy.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Secure Authentication Token.
 * Replaces the original Spring Security Token, hiding real data (PII).
 */
public class PseudonymizedAuthentication extends AbstractAuthenticationToken {

    private final String pseudonym;
    private final String originalToken;

    public PseudonymizedAuthentication(String pseudonym, String originalToken, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.pseudonym = pseudonym;
        this.originalToken = originalToken;
        super.setAuthenticated(true);
    }

    @Override
    public @Nullable Object getCredentials() {
        return originalToken;
    }

    @Override
    public Object getPrincipal() {
        return pseudonym;
    }
}
