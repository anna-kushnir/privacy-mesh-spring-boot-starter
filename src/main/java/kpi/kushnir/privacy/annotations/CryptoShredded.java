package kpi.kushnir.privacy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for automatic encryption/decryption of data.
 * If the encryption key (DEK) is deleted,
 * the field will be forever transformed into cryptographic noise.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CryptoShredded {
}
