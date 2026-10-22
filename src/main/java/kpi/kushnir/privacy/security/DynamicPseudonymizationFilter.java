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
    private final String applicationName;

    public DynamicPseudonymizationFilter(PseudonymResolver pseudonymResolver, String applicationName) {
        this.pseudonymResolver = pseudonymResolver;
        this.applicationName = applicationName;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/register") ||
                path.startsWith("/api/v1/auth/login") ||
                path.startsWith("/api/v1/auth/verify-2fa");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String dpopProof = request.getHeader("DPoP");
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && (authHeader.startsWith("DPoP ") || authHeader.startsWith("Bearer "))) {
            String token = authHeader.substring(authHeader.indexOf(" ") + 1);

            try {
                PseudonymizedAuthentication auth = pseudonymResolver.resolveAndAuthenticate(token, dpopProof, applicationName);
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (AccessDeniedException e) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Access Denied\", \"message\": \"" + e.getMessage() + "\"}");
                return;
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + e.getMessage() + "\"}");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
