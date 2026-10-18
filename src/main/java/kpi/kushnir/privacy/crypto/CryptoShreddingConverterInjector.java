package kpi.kushnir.privacy.crypto;

import org.springframework.stereotype.Component;

@Component
public class CryptoShreddingConverterInjector {
    public CryptoShreddingConverterInjector(KmsClient kmsClient, EncryptionUtils encryptionUtils) {
        CryptoShreddingConverter.setDependencies(kmsClient, encryptionUtils);
    }
}
