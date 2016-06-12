/**
 * Enum class to represent each authentication service.
 */
public enum AuthType {
    FACEBOOK("facebook"),
    GOOGLE("google");

    private String name;

    /**
     * constructor
     * @param val
     */
    AuthType(String val) {
        this.name = val;
    }

    /**
     * return the string value of the enum
     * @return string
     */
    public String getName() {
        return name;
    }

}
