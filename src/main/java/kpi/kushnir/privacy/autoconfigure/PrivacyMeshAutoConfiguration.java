package kpi.kushnir.privacy.autoconfigure;

import kpi.kushnir.privacy.crypto.CryptoShreddingConverterInjector;
import kpi.kushnir.privacy.crypto.EncryptionUtils;
import kpi.kushnir.privacy.crypto.KmsClient;
import kpi.kushnir.privacy.crypto.LocalInMemoryKmsClient;
import kpi.kushnir.privacy.neo4j.UserIdentityRepository;
import kpi.kushnir.privacy.security.PseudonymResolver;
import kpi.kushnir.privacy.security.DynamicPseudonymizationFilter;
import kpi.kushnir.privacy.security.RiskBasedMfaEnforcer;
import org.neo4j.driver.Driver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * The main library configuration class (Starter).
 * It automatically registers all security components when the developer connects this module.
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(PrivacyMeshProperties.class)
@EnableNeo4jRepositories(
        considerNestedRepositories = true,
        basePackages = "kpi.kushnir.privacy.neo4j",
        transactionManagerRef = "neo4jTransactionManager")
public class PrivacyMeshAutoConfiguration {

    @Bean(name = "neo4jTransactionManager")
    public Neo4jTransactionManager neo4jTransactionManager(Driver driver) {
        return new Neo4jTransactionManager(driver);
    }

    @Bean
    @ConditionalOnMissingBean
    public RiskBasedMfaEnforcer riskBasedMfaEnforcer(PrivacyMeshProperties properties) {
        return new RiskBasedMfaEnforcer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public PseudonymResolver pseudonymResolver(UserIdentityRepository repository, JwtDecoder jwtDecoder, RiskBasedMfaEnforcer mfaEnforcer) {
        return new PseudonymResolver(repository, jwtDecoder, mfaEnforcer);
    }

    @Bean
    @ConditionalOnMissingBean
    public DynamicPseudonymizationFilter pseudonymizationFilter(
            PseudonymResolver resolver,
            @Value("${spring.application.name:default-service}") String applicationName) {
        return new DynamicPseudonymizationFilter(resolver, applicationName);
    }

    @Bean
    public FilterRegistrationBean<DynamicPseudonymizationFilter> registration(DynamicPseudonymizationFilter filter) {
        FilterRegistrationBean<DynamicPseudonymizationFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    @ConditionalOnMissingBean
    public KmsClient kmsClient() {
        return new LocalInMemoryKmsClient();
    }

    @Bean
    @ConditionalOnMissingBean
    public EncryptionUtils encryptionUtils() {
        return new EncryptionUtils();
    }

    @Bean
    public CryptoShreddingConverterInjector cryptoShreddingConverterInjector(KmsClient kmsClient, EncryptionUtils encryptionUtils) {
        return new CryptoShreddingConverterInjector(kmsClient, encryptionUtils);
    }
}
