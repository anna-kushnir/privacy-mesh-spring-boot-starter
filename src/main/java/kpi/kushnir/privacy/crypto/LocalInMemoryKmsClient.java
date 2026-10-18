package kpi.kushnir.privacy.crypto;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Local KMS implementation.
 * In production, this client should be replaced with a real integration with AWS KMS or HashiCorp Vault.
 */
public class LocalInMemoryKmsClient implements KmsClient {

    private final Map<String, String> keyVault = new ConcurrentHashMap<>();

    @Override
    public String getDekForPseudonym(String pseudonym) {
        return keyVault.computeIfAbsent(pseudonym, p -> generateNewAesKey());
    }

    @Override
    public void revokeKey(String pseudonym) {
        keyVault.remove(pseudonym);
    }

    private String generateNewAesKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate AES key", e);
        }
    }
}
