import com.github.scribejava.core.model.OAuth2AccessToken;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;


/**
 * Created by OdedA on 31-May-16.
 */
@Aspect
public class PermanentAuthAspect {

    @Pointcut("execution(* AspectUtils.loggedIn(..))")
    public void SomeAspectLoggedInInvoke() {}

    @After("SomeAspectLoggedInInvoke()")
    public void savePermAuthToDisk(JoinPoint point) {
        System.out.println("In permanent Auth manager aspect. about to save auth token to disk.");
        OAuth2AccessToken token = (OAuth2AccessToken)point.getArgs()[0];
        AuthType authType = (AuthType)point.getArgs()[1];
        AuthToken authToken = new AuthToken(token, authType);
        if (!AspectUtils.writeAuthToFile(authType.toString().toLowerCase(), authToken)) {
            System.out.println("auth token failed to save locally.");
        } else {
            System.out.println("auth token saved successfully on disk");
        }
    }

    @Pointcut("execution(* AspectUtils.attemptingLogIn(..))")
    public void SomeAspectAttemptingToLogInInvoke() {}

    @Around("SomeAspectAttemptingToLogInInvoke()")
    public OAuth2AccessToken getPermAuthFromDisk(ProceedingJoinPoint point) {
        System.out.println("Aspect checking if an available auth is in file");
        AuthType authType = (AuthType)point.getArgs()[0];
        AuthToken authToken = AspectUtils.readAuthFromFile(authType.toString().toLowerCase());
        if (authToken != null) {
            try {
                if (authToken.type == AuthType.FACEBOOK) {
                    return authToken.token;
                } else {
                    point.proceed();
                }
            } catch (Throwable t) {
                System.err.println(t);
            }
        }
        return null;
    }
}
