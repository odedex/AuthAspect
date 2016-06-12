import com.github.scribejava.core.model.OAuth2AccessToken;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;

import java.util.Date;

/**
 * Aspect to read/write an authentication token from/to the disk.
 * When read, this aspects validates the token was written no more than 1 minute ago.
 */
@Aspect
public class OneMinuteAuthAspect {

    private static boolean oneMinActivate;

    /**
     * Constructor.
     */
    public OneMinuteAuthAspect() {
        oneMinActivate = false;
    }

    /**
     * Pointcut for the execution of AspectUtils.attemptingLogIn(). This pointcut triggers only
     * if a private boolean member is true.
     * @return true iff oneMinActivate is true
     */
    @Pointcut("execution(* AspectUtils.attemptingLogIn(..)) && if()")
    public static boolean OneMinAuthSomeAspectAttemptingToLogInInvoke() {
        return oneMinActivate;
    }

    /**
     * Around the execution of AspectUtils.attemptingLogIn(), check if a token of the relevant authentication
     * is already available on the disk, and check if the timestamp is no more than 1 minute old.
     * If it is, return that token. Otherwise return null.
     * @param point ProceedingJoinPoint
     * @return OAuth2AccessToken if a valid one found. null otherwise.
     */
    @Around("OneMinAuthSomeAspectAttemptingToLogInInvoke()")
    public OAuth2AccessToken getOneMinAuthFromDisk(ProceedingJoinPoint point) {

        System.out.println("Aspect checking if an available auth is in file");
        AuthType authType = (AuthType)point.getArgs()[0];
        AuthToken authToken = AspectUtils.readAuthFromFile(authType.toString().toLowerCase());
        if (authToken != null) {
            System.out.println("token found on disk. checking if it is no more than 1 minute old");
            Date now = new Date();
            long difference = now.getTime() - authToken.date.getTime();
            System.out.println(difference / 1000);
            if (difference / 1000 <= 60) {
                System.out.println("token is good!");
                if (authToken.type == authType) {
                    return authToken.token;
                }
//                System.out.println("token was bad.");
            } else {
                System.out.println("existing token is too old");
            }
        }
        return null;
    }

    /**
     * Pointcut for the execution of AspectUtils.loggedIn(). This pointcut triggers only
     * if a private boolean member is true.
     * @return true iff oneMinActivate is true
     */
    @Pointcut("execution(* AspectUtils.loggedIn(..)) && if()")
    public static boolean OneMinAuthSomeAspectLoggedInInvoke() {
        return oneMinActivate;
    }

    /**
     * After the execution of AspectUtils.loggedIn(), create a wrapper object for the valid token
     * and write it to the disk.
     * @param point JoinPoint
     */
    @After("OneMinAuthSomeAspectLoggedInInvoke()")
    public void saveOneMinAuthToDisk(JoinPoint point) {

//        System.out.println("In one minute auth aspect. about to save auth token to disk.");
        OAuth2AccessToken token = (OAuth2AccessToken)point.getArgs()[0];
        AuthType authType = (AuthType)point.getArgs()[1];
        AuthToken authToken = new AuthToken(token, authType);
        if (!AspectUtils.writeAuthToFile(authType.toString().toLowerCase(), authToken)) {
//            System.out.println("auth token failed to save locally.");
        } else {
//            System.out.println("auth token saved successfully on disk");
        }

    }

    /**
     * Before an anonymous pointcut for the execution of any function with annotation @OneMinAuth and any auth
     * annotation, set a local boolean member to true, effectively turning this aspect on.
     */
    @Before("@annotation(OneMinAuth) && (@annotation(FacebookAuth) || @annotation(GoogleAuth)) && execution(* *(..))")
    public void initOneMinAuthAspect() {
//        System.out.println("one minute auth is now used");
        oneMinActivate = true;
    }

    /**
     * Pointcut for the execution of AspectUtils.finishedLogIn(). Pointcut triggers only if a private boolean
     * is true.
     * @return true iff oneMinActivate is true.
     */
    @Pointcut("execution(* AspectUtils.finishedLogIn(..)) && if()")
    public static boolean OneMinAuthSomeAspectFinishedLoggingInInvoke() {
        return oneMinActivate;
    }

    /**
     * Before the execution of AspectUtils.finishedLogIn(), set the private member to false, effectively
     * turning this aspect off.
     */
    @Before("OneMinAuthSomeAspectFinishedLoggingInInvoke()")
    public void finishOneMinAuthAspect() {
//        System.out.println("one minute auth is no longer used");
        oneMinActivate = false;
    }
}
