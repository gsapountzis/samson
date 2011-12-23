package samson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Data type conversion result: Either[Cause, Value]
 */
public class Conversion {

    private final Annotation[] annotations;
    private final Type type;
    private final Class<?> clazz;

    /** An error occured while setting the value to the target object */
    private final boolean error;
    private final Throwable cause;
    private final Object value;

    private Conversion(Annotation[] annotations, Type type, Class<?> clazz, boolean error, Throwable cause, Object value) {
        this.annotations = annotations;
        this.type = type;
        this.clazz = clazz;

        this.error = error;
        this.cause = cause;
        this.value = value;
    }

    public static Conversion fromError(Annotation[] annotations, Type type, Class<?> clazz, Throwable cause) {
        return new Conversion(annotations, type, clazz, true, cause, null);
    }

    public static Conversion fromValue(Annotation[] annotations, Type type, Class<?> clazz, Object value) {
        return new Conversion(annotations, type, clazz, false, null, value);
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public Type getType() {
        return type;
    }

    public Class<?> getRawType() {
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
