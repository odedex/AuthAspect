
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by OdedA on 06-Mar-16.
 */
@Aspect
public class LoggingAspect {
//    //test logging pointcut
//    pointcut publicCalls() : call (public void Increase(..));
//
//    before() : publicCalls() {
//        System.out.println("Calling increase Method");
//    }


//    @Pointcut("execution(public * *(..))")
//    public void publicMethod() {}

    @Pointcut ("execution(@Logging * *(..))")
    public void loggingAnnot() {}

//    @Pointcut("execution(* Counter.Increase(..))")
//    public void counterIncrease() {}

    @Before("loggingAnnot()")
    public void beforeLoggingAnnot (JoinPoint joinPoint) {
        System.out.println(joinPoint.toString());
//        System.out.println("Called Logging annotaion method");
    }


//    // log message on every button click
//    pointcut calcButton() : execution (public void actionPerformed(..));
//
//    before() : calcButton() {
//        System.out.println("calling public calculator button");
//    }


//    // ask for input of 'y' on every calculator button press
//    pointcut calcButton() : execution (public void actionPerformed(..));
//
//    void around() : calcButton() {
//        System.out.println("please enter 'y' to continue");
//
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        try{
//            String s = br.readLine();
//            if (s.equals("y")) {
//                proceed();
//            }
//        }catch(Exception e){
//            System.err.println("Invalid Format!");
//        }
//    }

}