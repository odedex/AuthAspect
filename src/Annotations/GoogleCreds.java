package Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Nadav on 31/05/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GoogleCreds {
    String clientId();
    String secret();
    String scope();
}
