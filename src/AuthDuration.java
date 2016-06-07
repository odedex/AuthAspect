public enum AuthDuration {
    PERM("perm"),
    WEEK("week");

    private String name;

    private AuthDuration(String val) {
        this.name = val;
    }

    public String getName() {
        return name;
    }

}