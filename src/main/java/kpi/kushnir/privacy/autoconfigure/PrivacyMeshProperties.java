package kpi.kushnir.privacy.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * A class for reading the library configuration from the application.yml file of the end application.
 * The "privacy-mesh" prefix allows developers to configure our library.
 */
@Component
@ConfigurationProperties(prefix = "privacy-mesh")
public class PrivacyMeshProperties {

    private Map<String, RiskLevel> mfaPolicies = new HashMap<>();

    public Map<String, RiskLevel> getMfaPolicies() {
        return mfaPolicies;
    }

    public void setMfaPolicies(Map<String, RiskLevel> mfaPolicies) {
        this.mfaPolicies = mfaPolicies;
    }

    public enum RiskLevel {
        LOW, MEDIUM, HIGH
    }
}
