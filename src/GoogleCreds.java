import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation containing the API keys for a google authentication. Used by GoogleAuthAspect
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GoogleCreds {
    String clientId();
    String secret();
    String scope();
}
