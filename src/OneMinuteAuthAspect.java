import com.github.scribejava.core.model.OAuth2AccessToken;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Date;

/**
 * Created by OdedA on 08-Jun-16.
 */
@Aspect
public class OneMinuteAuthAspect {

    private static boolean oneMinActivate;

    public OneMinuteAuthAspect() {
        System.out.println("in one minute auth ctor");

//        System.out.println("=================================");
//        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
//            System.out.println(ste);
//        }
//        System.out.println("=================================");

        oneMinActivate = false;
    }

    @Pointcut("execution(* AspectUtils.attemptingLogIn(..)) && if()")
    public static boolean OneMinAuthSomeAspectAttemptingToLogInInvoke() {
        return oneMinActivate;
    }

    @Around("OneMinAuthSomeAspectAttemptingToLogInInvoke()")
    public OAuth2AccessToken getOneMinAuthFromDisk(ProceedingJoinPoint point) {
        if (!oneMinActivate) {
            try {
                point.proceed();
            } catch (Throwable t) {
                System.err.println(t);
            }
            System.out.println("returning null from one min auth aspect");
            return null;
        }
        System.out.println("Aspect checking if an available auth is in file");
        AuthType authType = (AuthType)point.getArgs()[0];
        AuthToken authToken = AspectUtils.readAuthFromFile(authType.toString().toLowerCase());
        if (authToken != null) {
            System.out.println("token found on disk. checking if it is no more than 1 minute old");
            Date now = new Date();
            long difference = now.getTime() - authToken.date.getTime();
            System.out.println(difference / 1000);
            if (difference / 1000 <= 60) {
                System.out.println("token is no more than 60 seconds old. continuing...");
                if (authToken.type == authType) {
                    return authToken.token;
                }
                System.out.println("token was bad.");
            } else {
                System.out.println("token is too old");
            }
        }
        return null;
    }

    @Pointcut("execution(* AspectUtils.loggedIn(..)) && if()")
    public static boolean OneMinAuthSomeAspectLoggedInInvoke() {
        return oneMinActivate;
    }

    @After("OneMinAuthSomeAspectLoggedInInvoke()")
    public void saveOneMinAuthToDisk(JoinPoint point) {
        if (!oneMinActivate) {
            return;
        }
        System.out.println("In one minute auth aspect. about to save auth token to disk.");
        OAuth2AccessToken token = (OAuth2AccessToken)point.getArgs()[0];
        AuthType authType = (AuthType)point.getArgs()[1];
        AuthToken authToken = new AuthToken(token, authType);
        if (!AspectUtils.writeAuthToFile(authType.toString().toLowerCase(), authToken)) {
            System.out.println("auth token failed to save locally.");
        } else {
            System.out.println("auth token saved successfully on disk");
        }

    }


    @Before("@annotation(OneMinAuth) && execution(* *(..))")
    public void initOneMinAuthAspect() {
        System.out.println("one minute auth is now used");
//        System.out.println("=================================");
//        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
//            System.out.println(ste);
//        }
//        System.out.println("=================================");
        oneMinActivate = true;
    }

    @Pointcut("execution(* AspectUtils.finishedLogIn(..))")
    public void OneMinAuthSomeAspectFinishedLoggingInInvoke() {}

    @Before("OneMinAuthSomeAspectFinishedLoggingInInvoke()")
    public void finishOneMinAuthAspect() {
        if (oneMinActivate) {
            System.out.println("one minute auth is no longer used");
            oneMinActivate = false;
        }
    }
}
