package kpi.kushnir.privacy.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * Graph DB Model: Pseudonym Node.
 * Stores a secure UUID that is given to the microservice.
 */
@Node("Pseudonym")
public class PseudonymNode {

    @Id
    private final String pseudonymValue;

    public PseudonymNode(String pseudonymValue) {
        this.pseudonymValue = pseudonymValue;
    }

    public String getPseudonymValue() {
        return pseudonymValue;
    }
}
