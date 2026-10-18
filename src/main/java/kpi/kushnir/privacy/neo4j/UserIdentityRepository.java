package kpi.kushnir.privacy.neo4j;

import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for accessing Neo4j.
 * Includes native Cypher queries for search and cascade delete (GDPR Art. 17).
 */
@Repository
public interface UserIdentityRepository extends Neo4jRepository<UserIdentityNode, String> {

    /**
     * Pseudonym search by real ID and context (microservice) name.
     */
    @Query("MATCH (u:UserIdentity {realUserId: $realUserId})-[r:HAS_PSEUDONYM {contextName: $contextName}]->(p:Pseudonym) RETURN p.pseudonymValue")
    Optional<String> findPseudonymValueByContext(String realUserId, String contextName);

    /**
     * GDPR Article 17: Deleting a user and all his pseudonyms.
     */
    @Query("MATCH (u:UserIdentity {realUserId: $realUserId}) DETACH DELETE u")
    void permanentlyEraseUserIdentity(String realUserId);
}
