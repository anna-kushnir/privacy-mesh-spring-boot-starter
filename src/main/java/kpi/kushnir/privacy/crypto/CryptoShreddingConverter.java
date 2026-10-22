package kpi.kushnir.privacy.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kpi.kushnir.privacy.security.SecurityContextHelper;

import java.util.Optional;

/**
 * Automatic Spring Data JPA converter.
 * It works when saving (INSERT/UPDATE) and reading (SELECT) from the database.
 */
@Converter
public class CryptoShreddingConverter implements AttributeConverter<String, String> {

    private static KmsClient kmsClient;
    private static EncryptionUtils encryptionUtils;

    public static void setDependencies(KmsClient kmsClient, EncryptionUtils encryptionUtils) {
        CryptoShreddingConverter.kmsClient = kmsClient;
        CryptoShreddingConverter.encryptionUtils = encryptionUtils;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        Optional<String> pseudonymOpt = SecurityContextHelper.getCurrentUserPseudonym();
        if (pseudonymOpt.isEmpty() || kmsClient == null) {
            return attribute;
        }

        try {
            String pseudonym = pseudonymOpt.get();
            String dek = kmsClient.getDekForPseudonym(pseudonym);
            return encryptionUtils.encrypt(attribute, dek);
        } catch (Exception e) {
            throw new RuntimeException("Refusing to write plaintext PII due to encryption failure", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }

        Optional<String> pseudonymOpt = SecurityContextHelper.getCurrentUserPseudonym();
        if (pseudonymOpt.isEmpty() || kmsClient == null) {
            return dbData;
        }

        try {
            String pseudonym = pseudonymOpt.get();
            String dek = kmsClient.getDekForPseudonym(pseudonym);
            return encryptionUtils.decrypt(dbData, dek);
        } catch (KeyRevokedException e) {
            return "[DATA_SHREDDED]";
        } catch (Exception e) {
            return dbData;
        }
    }
}
