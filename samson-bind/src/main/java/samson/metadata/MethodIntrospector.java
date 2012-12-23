package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class MethodIntrospector {

    static MethodMetadata introspect(Method method) {
        return new MethodIntrospector().apply(method);
    }

    private MethodIntrospector() {
    }

    private MethodMetadata apply(Method method) {
        List<MethodParameter> parameters = new ArrayList<MethodParameter>();

        Annotation[][] annotations = method.getParameterAnnotations();
        Type[] genericTypes = method.getGenericParameterTypes();
        Class<?>[] types = method.getParameterTypes();

        int length = types.length;
        for (int i = 0; i < length; i++) {
            TypeClassPair tcp = new TypeClassPair(genericTypes[i], types[i]);
            MethodParameter parameter = new MethodParameter(annotations[i], tcp, method, i, null);
            parameters.add(parameter);
        }

        return new MethodMetadata(method, parameters);
    }

}