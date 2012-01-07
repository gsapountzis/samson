package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class MethodParameter extends Element {

    public final Method method;
    public final int parameterIndex;

    public MethodParameter(Element element, Method method, int parameterIndex) {
        super(element.annotations, element.tcp, element.name, element.encoded, element.defaultValue);
        this.method = method;
        this.parameterIndex = parameterIndex;
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
