package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MethodParameter extends Element {

    public final Method method;
    public final int parameterIndex;
    public final String parameterName;

    public MethodParameter(Annotation[] annotations, TypeClassPair tcp, Method method, int parameterIndex, String parameterName) {
        super(annotations, tcp);
        this.method = method;
        this.parameterIndex = parameterIndex;
        this.parameterName = parameterName;
    }

    /**
     * Check if the method parameter is annotated with a specific annotation.
     */
    public boolean hasAnnotation(Annotation annotation) {
        for (Annotation a : annotations) {
            if (a.equals(annotation)) {
                return true;
            }
        }
        return false;
    }

}
