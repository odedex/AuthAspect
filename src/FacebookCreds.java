import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation containing the API keys for a facebook authentication. Used by FacebookAuthAspect
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FacebookCreds  {
    String clientId();
    String secret();
    String scope();
}




