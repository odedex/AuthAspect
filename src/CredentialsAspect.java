import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.annotation.Annotation;

/**
 * Aspect to find the annotations containing the authentication services API keys
 */
@Aspect
public class CredentialsAspect {

    public static Annotation[] annotations;

    /**
     * Pointcut for the execution of public static void main
     */
    @Pointcut("execution(public static void main(String[]))")
    public void FacebookAuthPublicStaticVoidMainInvoke(){}

    /**
     * Before the execution of public static void main, parse out the name of the class containing the call
     * and use reflection to get all annotations of that class.
     * @param point JoinPoint
     */
    @Before("FacebookAuthPublicStaticVoidMainInvoke()")
    public void BeforeFacebookAuthPublicStaticVoidMainInvoke(JoinPoint point) {

        String psvmSignature = point.getStaticPart().getSignature().toString();
        String[] psvmSigArrayByDots = psvmSignature.split("\\.");
        String callingClass = psvmSigArrayByDots[psvmSigArrayByDots.length - 2];
        String[] callingClassArrayBySpaces = callingClass.split(" ");
        String classname = callingClassArrayBySpaces[callingClassArrayBySpaces.length - 1];

        try {
            Class mainclass = Class.forName(classname);
            annotations = mainclass.getAnnotations();

//            System.out.println("got all credential annotations!");
        } catch (ClassNotFoundException cnfe) {
            System.err.println(cnfe);
            System.out.println("could not locate main class for program");
        }
    }
}
