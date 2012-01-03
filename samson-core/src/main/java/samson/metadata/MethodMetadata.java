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

    /**
     * Find the <em>first</em> method parameter annotated with a specific annotation.
     * <p>
     * <b>ATT:</b> The method is used for finding method parameters by their &#064;FormParam
     * or &#064;QueryParam annotation and assumes that the parameters names as specified in
     * the parameter annotations are unique in each method.
     */
    public MethodParameter findParameter(Annotation annotation) {
        for (MethodParameter parameter : parameters) {
            if (parameter.hasAnnotation(annotation)) {
                return parameter;
            }
        }
        return null;
    }

    public MethodParameter getParameter(int parameterIndex) {
        return parameters.get(parameterIndex);
    }

    public List<MethodParameter> getParameters() {
        return parameters;
    }

}
