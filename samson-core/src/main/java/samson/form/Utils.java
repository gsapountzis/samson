package samson.form;

import java.util.List;

class Utils {

    private Utils() { }

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.trim().length() == 0);
    }

    public static <T> T getFirst(List<T> values) {
        if (values != null && values.size() > 0) {
            return values.get(0);
        } else {
            return null;
        }
    }

}
