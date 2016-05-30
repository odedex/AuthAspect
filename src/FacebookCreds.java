import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by OdedA on 30-May-16.
 */
@Retention(RetentionPolicy.RUNTIME)

public @interface FacebookCreds  {
    String clientId();
    String secret();
}




