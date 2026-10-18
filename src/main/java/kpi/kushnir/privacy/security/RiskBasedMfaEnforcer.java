package kpi.kushnir.privacy.security;

import kpi.kushnir.privacy.autoconfigure.PrivacyMeshProperties;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.nio.file.AccessDeniedException;
import java.util.List;

/**
 * Component for implementing tiered dynamic MFA.
 * Analyzes the risk level of a microservice (based on application configuration) and requires appropriate factors from the token.
 */
@Component
public class RiskBasedMfaEnforcer {

    private final PrivacyMeshProperties properties;

    public RiskBasedMfaEnforcer(PrivacyMeshProperties properties) {
        this.properties = properties;
    }

    public void enforceMfaPolicy(Jwt jwt, String contextName) throws AccessDeniedException {
        PrivacyMeshProperties.RiskLevel requiredRiskLevel = properties.getMfaPolicies()
                .getOrDefault(contextName, PrivacyMeshProperties.RiskLevel.MEDIUM);

        List<String> amrClaims = jwt.getClaimAsStringList("amr");

        if (amrClaims == null) {
            amrClaims = List.of();
        }

        switch (requiredRiskLevel) {
            case HIGH:
                if (!amrClaims.contains("webauthn") && !amrClaims.contains("hwk")) {
                    throw new AccessDeniedException("High-risk context requires Biometric/Hardware MFA (WebAuthn).");
                }
                break;

            case MEDIUM:
                if (!amrClaims.contains("otp") && !amrClaims.contains("webauthn")) {
                    throw new AccessDeniedException("Medium-risk context requires at least TOTP (Authenticator App).");
                }
                break;

            case LOW:
                break;
        }
    }

}
