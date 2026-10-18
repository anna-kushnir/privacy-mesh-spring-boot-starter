package kpi.kushnir.privacy.neo4j;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * Graph DB Model: User Root Node.
 * Stores the real ID and all pseudonym relationships.
 */
@Node("UserIdentity")
public class UserIdentityNode {

    @Id
    private final String realUserId;

    @Relationship(type = "HAS_PSEUDONYM", direction = Relationship.Direction.OUTGOING)
    private final Set<ContextPseudonymRelation> pseudonyms = new HashSet<>();

    public UserIdentityNode(String realUserId) {
        this.realUserId = realUserId;
    }

    public String getRealUserId() {
        return realUserId;
    }
    public Set<ContextPseudonymRelation> getPseudonyms() {
        return pseudonyms;
    }

    public void addPseudonym(ContextPseudonymRelation relation) {
        pseudonyms.add(relation);
    }
}
