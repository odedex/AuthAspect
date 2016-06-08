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


    @Pointcut("execution(* AspectUtils.attemptingLogIn(..)) && if()")
    public static boolean PermAuthSomeAspectAttemptingToLogInInvoke() {
        return permActivate;
    }

    @Around("PermAuthSomeAspectAttemptingToLogInInvoke()")
    public OAuth2AccessToken getPermAuthFromDisk(ProceedingJoinPoint point) {
        if (!permActivate) {
            try {
                point.proceed();
            } catch (Throwable t) {
                System.err.println(t);
            }
            System.out.println("returning null from perm auth aspect");
            return null;
        }
        System.out.println("Aspect checking if an available auth is in file");
        AuthType authType = (AuthType)point.getArgs()[0];
        AuthToken authToken = AspectUtils.readAuthFromFile(authType.toString().toLowerCase());
        if (authToken != null) {
            if (authToken.type == authType) {
                System.out.println("found good token!");
                return authToken.token;
            } else {
                System.out.println("token of bad type was read from file");
            }
        } else {
            System.out.println("no token read from file");
        }
        System.out.println("RETURNING NULL FROM PERM AUTH ASPECT");
        return null;
    }

    @Pointcut("execution(* AspectUtils.loggedIn(..)) && if()")
    public static boolean PermAuthSomeAspectLoggedInInvoke() {
        return permActivate;
    }

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
