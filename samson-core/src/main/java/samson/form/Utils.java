package samson.form;

import java.util.List;

class Utils {

    private Utils() { }

    public static boolean isNullOrEmpty(String s) {
        return (s == null || s.isEmpty());
    }

    public static <T> T getFirst(List<T> values) {
        if (!(values == null || values.isEmpty())) {
            return values.get(0);
        } else {
            return null;
        }
    }

}
