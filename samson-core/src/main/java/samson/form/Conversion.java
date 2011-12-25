package samson.form;

import samson.convert.ConverterException;

/**
 * Data type conversion result: Either[Cause, Value]
 */
class Conversion {

    /** An error occured while setting the value to the target object */
    private final boolean error;
    private final ConverterException cause;
    private final Object value;

    private Conversion(boolean error, ConverterException cause, Object value) {
        this.error = error;
        this.cause = cause;
        this.value = value;
    }

    public static Conversion fromError(ConverterException cause) {
        return new Conversion(true, cause, null);
    }

    public static Conversion fromValue(Object value) {
        return new Conversion(false, null, value);
    }

    public boolean isError() {
        return error;
    }

    public ConverterException getCause() {
        return cause;
    }

    public Object getValue() {
        return value;
    }

}
