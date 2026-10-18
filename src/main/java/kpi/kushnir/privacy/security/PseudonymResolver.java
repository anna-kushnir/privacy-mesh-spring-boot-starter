package kpi.kushnir.privacy.security;

import kpi.kushnir.privacy.neo4j.ContextPseudonymRelation;
import kpi.kushnir.privacy.neo4j.PseudonymNode;
import kpi.kushnir.privacy.neo4j.UserIdentityNode;
import kpi.kushnir.privacy.neo4j.UserIdentityRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

/**
 * Business logic for decrypting the token and accessing the pseudonymization domain (Neo4j).
 */
@Service
public class PseudonymResolver {

    private final UserIdentityRepository repository;
    private final JwtDecoder jwtDecoder;
    private final RiskBasedMfaEnforcer mfaEnforcer;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public PseudonymResolver(UserIdentityRepository repository, JwtDecoder jwtDecoder, RiskBasedMfaEnforcer mfaEnforcer) {
        this.repository = repository;
        this.jwtDecoder = jwtDecoder;
        this.mfaEnforcer = mfaEnforcer;
    }

    @Transactional
    public PseudonymizedAuthentication resolveAndAuthenticate(String token, String dpopProof, String contextName)
            throws AccessDeniedException {
        validateDpopProof(dpopProof, token);
        Jwt jwt = jwtDecoder.decode(token);
        mfaEnforcer.enforceMfaPolicy(jwt, contextName);

        String realUserId = jwt.getSubject();

        String pseudonym = repository.findPseudonymValueByContext(realUserId, contextName)
                .orElseGet(() -> generateAndSaveNewPseudonym(realUserId, contextName));

        return new PseudonymizedAuthentication(pseudonym, token, null);
    }

    private void validateDpopProof(String dpopProof, String accessToken) {
        if (dpopProof == null || dpopProof.isBlank()) {
            throw new SecurityException("DPoP proof is missing or empty");
        }

        if (dpopProof.split("\\.").length != 3) {
            throw new SecurityException("Invalid DPoP proof format");
        }

        // TODO: implement corresponding to RFC 9449
    }

    private String generateAndSaveNewPseudonym(String realUserId, String contextName) {
        UserIdentityNode userNode = repository.findById(realUserId)
                .orElse(new UserIdentityNode(realUserId));

        String newPseudonym = UUID.randomUUID().toString();
        PseudonymNode pseudonymNode = new PseudonymNode(newPseudonym);

        ContextPseudonymRelation relation = new ContextPseudonymRelation(contextName, pseudonymNode);
        userNode.addPseudonym(relation);

        repository.save(userNode);
        return newPseudonym;
    }
}
