package samson.convert;

import samson.metadata.Element;

/**
 * Data type conversion result: Either[Cause, Value]
 */
public class Conversion {

    private final Element element;

    /** An error occured while setting the value to the target object */
    private final boolean error;

    private final Throwable cause;

    private final Object value;

    private Conversion(Element element, boolean error, Throwable cause, Object value) {
        this.element = element;
        this.error = error;
        this.cause = cause;
        this.value = value;
    }

    public static Conversion fromError(Element element, Throwable cause) {
        return new Conversion(element, true, cause, null);
    }

    public static Conversion fromValue(Element element, Object value) {
        return new Conversion(element, false, null, value);
    }

    public Element getElement() {
        return element;
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
