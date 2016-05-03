
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by OdedA on 06-Mar-16.
 */
public aspect LoggingAspect {
//    //test logging pointcut
//    pointcut publicCalls() : call (public void Increase(..));
//
//    before() : publicCalls() {
//        System.out.println("Calling increase Method");
//    }


//    @Pointcut("execution(* Counter.Increase(..))")
//    public void counterIncrease() {}
//
//    @Before("counterIncrease()")
//    public void beforeCounterIncrease (JoinPoint joinPoint) {
//        System.out.println("calling increase Method");
//    }



    @Pointcut("execution(@MonitorAspect * *(..))")
    public void monitored() {}

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {}

    @Pointcut("monitored() && publicMethod()")
    public void publicMethodInsideAClassMarkedWithAtMonitor() {}

    @Before("publicMethodInsideAClassMarkedWithAtMonitor()")
    public void beforeMonitored(JoinPoint joinPoint) {
        System.out.println("Clicked monitored function.");
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