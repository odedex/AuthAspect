import com.github.scribejava.core.model.OAuth2AccessToken;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;


/**
 * Created by OdedA on 31-May-16.
 */
@Aspect
public class PermanentAuthAspect {

    private static boolean permActivate;


    public PermanentAuthAspect() {
//        System.out.println("=================================");
        System.out.println("in perm auth ctor");
//        System.out.println(Thread.currentThread().getStackTrace().toString());
//        System.out.println("=================================");

        permActivate = false;
    }


    @Pointcut("execution(* AspectUtils.attemptingLogIn(..))")
    public void PermAuthSomeAspectAttemptingToLogInInvoke() {}

    @Around("PermAuthSomeAspectAttemptingToLogInInvoke()")
    public OAuth2AccessToken getPermAuthFromDisk(ProceedingJoinPoint point) {
        if (!permActivate) {
            try {
                point.proceed();
            } catch (Throwable t) {
                System.err.println(t);
            }
            return null;
        }
        System.out.println("Aspect checking if an available auth is in file");
        AuthType authType = (AuthType)point.getArgs()[0];
        AuthToken authToken = AspectUtils.readAuthFromFile(authType.toString().toLowerCase());
        if (authToken != null) {
            try {
                if (authToken.type == authType) {
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

    @Pointcut("execution(* AspectUtils.loggedIn(..))")
    public void PermAuthSomeAspectLoggedInInvoke() {}

    @After("PermAuthSomeAspectLoggedInInvoke()")
    public void savePermAuthToDisk(JoinPoint point) {
        if (!permActivate) {
            return;
        }
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

    @Before("@annotation(PermanentAuth) && execution(* *(..))")
    public void initPermAuthAspect() {
        System.out.println("perm auth is now used");
        permActivate = true;
    }

    @Pointcut("execution(* AspectUtils.finishedLogIn(..))")
    public void PermAuthSomeAspectFinishedLoggingInInvoke() {}

    @After("PermAuthSomeAspectFinishedLoggingInInvoke()")
    public void finishPermAuthAspect() {
        if (permActivate) {
            System.out.println("perm auth is no longer used");
            permActivate = false;
        }
    }


}
