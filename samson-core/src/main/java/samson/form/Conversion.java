package samson.form;

/**
 * Data type conversion result: Either[Cause, Value]
 */
class Conversion {

    /** An error occured while setting the value to the target object */
    private final boolean error;
    private final Throwable cause;
    private final Object value;

    private Conversion(boolean error, Throwable cause, Object value) {
        this.error = error;
        this.cause = cause;
        this.value = value;
    }

    public static Conversion fromError(Throwable cause) {
        return new Conversion(true, cause, null);
    }

    public static Conversion fromValue(Object value) {
        return new Conversion(false, null, value);
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
