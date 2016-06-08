import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

/**
 * Created by Oded on 08-Jun-16.
 */
@Aspect
@DeclarePrecedence("OneMinuteAuthAspect, PermanentAuthAspect, FacebookAuthAspect, GoogleAuthAspect")
public class UtilsAspect {
}
