package kpi.kushnir.privacy.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextHelper {
    /**
     * Gets the current user's pseudonym from the SecurityContext.
     * @return pseudonym (UUID) or throws an exception if the request is unauthorized.
     */
    public static String getCurrentUserPseudonym() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof PseudonymizedAuthentication) {
            return (String) auth.getPrincipal();
        }
        throw new IllegalStateException("Security context does not contain a pseudonymized authentication");
    }
}
