package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

public class MethodMetadata {

    private final Method method;
    private final List<MethodParameter> parameters;

    public MethodMetadata(Method method, List<MethodParameter> parameters) {
        this.method = method;
        this.parameters = parameters;
    }

    public Method getMethod() {
        return method;
    }

    public MethodParameter getParameter(int parameterIndex) {
        return parameters.get(parameterIndex);
    }

    public List<MethodParameter> getParameters() {
        return parameters;
    }

    /**
     * Find the method parameter annotated with a specific annotation.
     * <p>
     * This method is used for finding method parameters by their JAX-RS
     * parameter annotation and assumes that the parameters names as specified
     * in the JAX-RS parameter annotations are <em>unique</em> in each method.
     */
    public MethodParameter findParameter(Annotation annotation) {
        MethodParameter result = null;
        for (MethodParameter parameter : parameters) {
            if (parameter.hasAnnotation(annotation)) {
                if (result == null) {
                    result = parameter;
                }
                else {
                    throw new IllegalStateException("Multiple method parameters with the same annotation");
                }
            }
        }
        return result;
    }

}
