/**
 * Created by OdedA on 05-Jun-16.
 */
public enum AuthType {
    FACEBOOK("facebook"),
    GOOGLE("google");

    private String name;

    private AuthType(String val) {
        this.name = val;
    }

    public String getName() {
        return name;
    }

//    public String toString() {
//        return name;
//    }
}
