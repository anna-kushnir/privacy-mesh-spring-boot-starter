package kpi.kushnir.privacy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Security Filter (Interceptor).
 * Intercepts every HTTP request to the microservice, validates proof (DPoP) and performs pseudonymization.
 */
public class DynamicPseudonymizationFilter extends OncePerRequestFilter {

    private final PseudonymResolver pseudonymResolver;

    public DynamicPseudonymizationFilter(PseudonymResolver pseudonymResolver) {
        this.pseudonymResolver = pseudonymResolver;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String dpopProof = request.getHeader("DPoP");
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("DPoP ")) {
            String token = authHeader.substring(5);
            String contextName = request.getServerName();

            try {
                PseudonymizedAuthentication auth = pseudonymResolver.resolveAndAuthenticate(token, dpopProof, contextName);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (AccessDeniedException e) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "MFA not passed");
                return;
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token or DPoP proof");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
