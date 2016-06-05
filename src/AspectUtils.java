import java.util.ArrayList;

/**
 * Created by OdedA on 31-May-16.
 */
public class AspectUtils {
    public static void loggedIn(ArrayList<Object> data) {
        System.out.println("in AspectUtils.loggedIn");
        System.out.println(data);
    }
}
