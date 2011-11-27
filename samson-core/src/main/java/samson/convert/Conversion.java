package samson.convert;

/**
 * Data type conversion result: Either[Cause, Value]
 */
public class Conversion {

    private final Class<?> clazz;

    /** An error occured while setting the value to the target object */
    private final boolean error;

    private final Throwable cause;

    private final Object value;

    private Conversion(Class<?> clazz, boolean error, Throwable cause, Object value) {
        this.clazz = clazz;
        this.error = error;
        this.cause = cause;
        this.value = value;
    }

    public static Conversion fromError(Class<?> clazz, Throwable cause) {
        return new Conversion(clazz, true, cause, null);
    }

    public static Conversion fromValue(Class<?> clazz, Object value) {
        return new Conversion(clazz, false, null, value);
    }

    public Class<?> getTargetClass() {
        return clazz;
    }

    public boolean isError() {
        return error;
    }

    public Throwable getCause() {
        return cause;
    }

    public Object getValue() {
        return value;
    }

}
