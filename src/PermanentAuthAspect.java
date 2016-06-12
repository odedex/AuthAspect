import com.github.scribejava.core.model.OAuth2AccessToken;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;


/**
 * Aspect to read/write an authentication token from/to the disk.
 */
@Aspect
public class PermanentAuthAspect {

    private static boolean permActivate;

    /**
     * Constructor.
     */
    public PermanentAuthAspect() {
        permActivate = false;
    }

    /**
     * Pointcut for the execution of AspectUtils.attemptingLogIn(). This pointcut triggers only
     * if a private boolean member is true.
     * @return true iff permActivate is true
     */
    @Pointcut("execution(* AspectUtils.attemptingLogIn(..)) && if()")
    public static boolean PermAuthSomeAspectAttemptingToLogInInvoke() {
        return permActivate;
    }

    /**
     * Around the execution of AspectUtils.attemptingLogIn(), check if a token of the relevant authentication
     * is already available on the disk. If it is, return that token. Otherwise return null.
     * @param point ProceedingJoinPoint
     * @return OAuth2AccessToken if a valid one found. null otherwise.
     */
    @Around("PermAuthSomeAspectAttemptingToLogInInvoke()")
    public OAuth2AccessToken getPermAuthFromDisk(ProceedingJoinPoint point) {

        System.out.println("Aspect checking if an available auth is in file");
        AuthType authType = (AuthType)point.getArgs()[0];
        AuthToken authToken = AspectUtils.readAuthFromFile(authType.toString().toLowerCase());
        if (authToken != null) {
            if (authToken.type == authType) {
                System.out.println("found good token!");
                return authToken.token;
            } else {
//                System.out.println("token of bad type was read from file");
            }
        } else {
            System.out.println("no token read from file");
        }
//        System.out.println("RETURNING NULL FROM PERM AUTH ASPECT");
        return null;
    }

    /**
     * Pointcut for the execution of AspectUtils.loggedIn(). This pointcut triggers only
     * if a private boolean member is true.
     * @return true iff permActivate is true
     */
    @Pointcut("execution(* AspectUtils.loggedIn(..)) && if()")
    public static boolean PermAuthSomeAspectLoggedInInvoke() {
        return permActivate;
    }

    /**
     * After the execution of AspectUtils.loggedIn(), create a wrapper object for the valid token
     * and write it to the disk.
     * @param point JoinPoint
     */
    @After("PermAuthSomeAspectLoggedInInvoke()")
    public void savePermAuthToDisk(JoinPoint point) {
//        System.out.println("In permanent Auth manager aspect. about to save auth token to disk.");
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
     * Before an anonymous pointcut for the execution of any function with annotation @PermanentAuth and any auth
     * annotation, set a local boolean member to true, effectively turning this aspect on.
     */
    @Before("@annotation(PermanentAuth) && (@annotation(FacebookAuth) || @annotation(GoogleAuth)) && execution(* *(..))")
    public void initPermAuthAspect() {
//        System.out.println("perm auth is now used");
        permActivate = true;
    }

    /**
     * Pointcut for the execution of AspectUtils.finishedLogIn(). Pointcut triggers only if a private boolean
     * is true.
     * @return true iff permActivate is true.
     */
    @Pointcut("execution(* AspectUtils.finishedLogIn(..)) && if()")
    public static boolean PermAuthSomeAspectFinishedLoggingInInvoke() {
        return permActivate;
    }

    /**
     * Before the execution of AspectUtils.finishedLogIn(), set the private member to false, effectively
     * turning this aspect off.
     */
    @After("PermAuthSomeAspectFinishedLoggingInInvoke()")
    public void finishPermAuthAspect() {
            permActivate = false;
    }


}
