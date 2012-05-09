package samson.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

public class Utils {

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

    public static boolean isBaseType(Class<?> clazz) {

        if (clazz.isPrimitive()) {
            return true;
        }

        if (clazz == String.class ||
            clazz == Boolean.class ||
            clazz == Character.class ||
            clazz == Byte.class ||
            clazz == Short.class ||
            clazz == Integer.class ||
            clazz == Long.class ||
            clazz == Float.class ||
            clazz == Double.class ||
            clazz == BigInteger.class ||
            clazz == BigDecimal.class)
        {
            return true;
        }

        if (Enum.class.isAssignableFrom(clazz) ||
            Date.class.isAssignableFrom(clazz))
        {
            return true;
        }

        return false;
    }

}
