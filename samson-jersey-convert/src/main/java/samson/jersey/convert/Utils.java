package samson.jersey.convert;

class Utils {

    private Utils() { }

    public static boolean isNullOrEmpty(String s) {
        return (s == null || s.isEmpty());
    }

}
