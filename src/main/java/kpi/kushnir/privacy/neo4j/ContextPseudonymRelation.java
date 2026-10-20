package kpi.kushnir.privacy.neo4j;

import org.springframework.data.neo4j.core.schema.*;

/**
 * Graph DB Model: Relationship.
 * Indicates for which microservice (context) this pseudonym was generated.
 */
@RelationshipProperties
public class ContextPseudonymRelation {

    @Id
    @GeneratedValue
    private String id;

    private final String contextName;

    @TargetNode
    private final PseudonymNode pseudonymNode;

    public ContextPseudonymRelation(String contextName, PseudonymNode pseudonymNode) {
        this.contextName = contextName;
        this.pseudonymNode = pseudonymNode;
    }

    public String getContextName() {
        return contextName;
    }
    public PseudonymNode getPseudonymNode() {
        return pseudonymNode;
    }
}
