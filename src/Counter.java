
/**
 * Created by OdedA on 06-Mar-16.
 */

public class Counter {
    int curValue;

    public Counter() {
        curValue = 0;
    }

    @MonitorAspect
    public void Increase() {
        curValue++;
    }

    public void Decrease() {
        curValue--;
    }

    public int Get() {
        return curValue;
    }
}
