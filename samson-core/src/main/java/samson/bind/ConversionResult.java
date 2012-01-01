package samson.bind;

import samson.convert.ConverterException;

/**
 * Data type conversion result: Either[Cause, Value]
 */
class ConversionResult {

    /** An error occured while setting the value to the target object */
    private final boolean error;
    private final ConverterException cause;
    private final Object value;

    private ConversionResult(boolean error, ConverterException cause, Object value) {
        this.error = error;
        this.cause = cause;
        this.value = value;
    }

    public static ConversionResult fromError(ConverterException cause) {
        return new ConversionResult(true, cause, null);
    }

    public static ConversionResult fromValue(Object value) {
        return new ConversionResult(false, null, value);
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
