
/**
 * Created by OdedA on 06-Mar-16.
 */
public aspect LoggingAspect {
    pointcut publicCalls() : call (public void Increase(..));

    before() : publicCalls() {
        System.out.println("Calling increase Method");
    }
}