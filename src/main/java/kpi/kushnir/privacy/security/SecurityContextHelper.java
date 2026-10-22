package kpi.kushnir.privacy.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityContextHelper {
    /**
     * Gets the current user's pseudonym from the SecurityContext.
     * @return pseudonym (UUID) or throws an exception if the request is unauthorized.
     */
    public static Optional<String> getCurrentUserPseudonym() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof PseudonymizedAuthentication pseudonymizedAuth) {
            return Optional.ofNullable((String) pseudonymizedAuth.getPrincipal());
        }
        return Optional.empty();
    }
}
