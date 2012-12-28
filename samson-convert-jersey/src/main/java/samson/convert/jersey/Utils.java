package samson.convert.jersey;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

class Utils {

    private Utils() { }

    public static boolean isNullOrEmpty(String s) {
        return (s == null || s.isEmpty());
    }

    public static boolean isBaseType(Class<?> clazz) {

        if (clazz.isPrimitive()) {
            return true;
        }

        if (clazz == String.class ||
            clazz == Character.class ||
            clazz == Boolean.class ||
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
