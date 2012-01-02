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
