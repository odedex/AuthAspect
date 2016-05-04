
/**
 * Created by OdedA on 06-Mar-16.
 */

public class Counter {
    int curValue;

    public Counter() {
        curValue = 0;
    }

    @Logging
    public void Increase() {
        curValue++;
    }

    @Logging
    public void Decrease() {
        curValue--;
    }

    public int Get() {
        return curValue;
    }
}
