import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by OdedA on 04-May-16.
 */
@Aspect
public class BasicAuthAspect {


    @Pointcut("execution(@BasicAuth * *(..))")
    public void basicAuthAnnot() {}

//    @Pointcut("execution(public * *(..))")
//    public void publicMethod() {}

//    @Pointcut("basicAuthAnnot()")
//    public void publicMethodInsideAClassMarkedWithBasicAuth() {}

//    @Before("publicMethodInsideAClassMarkedWithBasicAuth()")
//    public void beforeMonitored(JoinPoint joinPoint) {
//        System.out.println("Clicked basicAuthAnnot function.");
//    }

    @Around("basicAuthAnnot()")
    public void aroundBasicAuthAnnot(ProceedingJoinPoint point) {

        System.out.println(point.toString());
        System.out.println("please enter 'y' to continue");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String s = br.readLine();
            if (s.equals("y")) {
                try {
                    point.proceed();
                } catch (Throwable t) {
                    System.out.println("caught throwable, refer to BasicAuthAspect");
                }
            }
        } catch (Exception e) {
            System.out.println("error");
        }
    }
}
