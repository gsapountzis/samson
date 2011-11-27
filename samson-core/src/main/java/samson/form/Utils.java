package samson.form;

import java.util.List;

class Utils {

    private Utils() { }

    public static boolean isEmpty(String s) {
        return (s != null) && (s.trim().length() == 0);
    }

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    public static String getFirst(List<String> values) {
        if (values != null && values.size() > 0) {
            return values.get(0);
        } else {
            return null;
        }
    }

}
