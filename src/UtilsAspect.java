import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclarePrecedence;

/**
 * Dummy aspect to declare aspect precedence in a neutral file,
 * and not in any other aspect as none of the existing ones can count as a "main" aspect.
 */
@Aspect
@DeclarePrecedence("CredentialsAspect, OneMinuteAuthAspect, PermanentAuthAspect, FacebookAuthAspect, GoogleAuthAspect")

public class UtilsAspect {
}
