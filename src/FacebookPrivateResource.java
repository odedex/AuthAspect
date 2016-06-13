import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for getting a resoudce from facebook graph by url.
 * place this annotation wherever you want to get the data. returns string.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FacebookPrivateResource {
    // url to access
    String url() default "";
}
