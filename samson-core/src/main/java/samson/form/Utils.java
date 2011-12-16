package samson.form;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class Utils {

    private Utils() { }

    public static boolean isEmpty(String s) {
        return (s != null) && (s.trim().length() == 0);
    }

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

    public static <T> T getFirst(Collection<T> values) {
        if (values != null) {
            Iterator<T> iterator = values.iterator();
            return iterator.hasNext() ? iterator.next() : null;
        } else {
            return null;
        }
    }

}
