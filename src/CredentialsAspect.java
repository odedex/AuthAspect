import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.annotation.Annotation;
import java.util.ArrayList;

/**
 * Created by Oded on 09-Jun-16.
 */

@Aspect
public class CredentialsAspect {

    public  static Annotation[] annotations;


    @Pointcut("execution(public static void main(String[]))")
    public void FacebookAuthPublicStaticVoidMainInvoke(){}

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
