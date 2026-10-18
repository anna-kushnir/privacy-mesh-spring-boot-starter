package kpi.kushnir.privacy.crypto;

public interface KmsClient {
    String getDekForPseudonym(String pseudonym);
    void revokeKey(String pseudonym);
}
