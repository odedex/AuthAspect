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
//        System.out.println("=================================");
        System.out.println("in one minute auth ctor");
//        System.out.println(Thread.currentThread().getStackTrace().toString());
//        System.out.println("=================================");

        oneMinActivate = false;
    }

    @Pointcut("execution(* AspectUtils.attemptingLogIn(..))")
    public void OneMinAuthSomeAspectAttemptingToLogInInvoke() {}

    @Around("OneMinAuthSomeAspectAttemptingToLogInInvoke()")
    public OAuth2AccessToken getOneMinAuthFromDisk(ProceedingJoinPoint point) {
        if (!oneMinActivate) {
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
            System.out.println("token found on disk. checking if it is no more than 1 minute old");
            Date now = new Date();
            long difference = now.getTime() - authToken.date.getTime();
            System.out.println(difference);
            if (difference / 1000 <= 60) {
                System.out.println("token is no more than 60 seconds old. continueing...");
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

        }
        return null;
    }

    @Pointcut("execution(* AspectUtils.loggedIn(..))")
    public void OneMinAuthSomeAspectLoggedInInvoke() {}

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


    @Pointcut("execution(@Annotations.OneMinuteAuth * *(..))")
    public void OneMinAuthAnnotationInvoke() {}

    @Before("OneMinAuthAnnotationInvoke()")
    public void initOneMinAuthAspect() {
        System.out.println("one minute auth is now used");
        oneMinActivate = true;
    }

    @Pointcut("execution(* AspectUtils.finishedLogIn(..))")
    public void OneMinAuthSomeAspectFinishedLoggingInInvoke() {}

    @After("OneMinAuthSomeAspectFinishedLoggingInInvoke()")
    public void finishOneMinAuthAspect() {
        if (oneMinActivate) {
            System.out.println("one minute auth is no longer used");
            oneMinActivate = false;
        }
    }
}
