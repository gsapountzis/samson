package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MethodMetadataCache {

    private final ConcurrentMap<Method, MethodMetadata> cache = new ConcurrentHashMap<Method, MethodMetadata>();

    public MethodMetadata get(final Method method) {

        MethodMetadata metadata = cache.get(method);
        if (metadata == null) {
            final MethodMetadata newMetadata = MethodIntrospector.createMethodMetadata(method);
            metadata = cache.putIfAbsent(method, newMetadata);
            if (metadata == null) {
                metadata = newMetadata;
            }
        }

        return metadata;
    }

    static class MethodIntrospector {

        static MethodMetadata createMethodMetadata(Method method) {
            List<MethodParameter> parameters = new ArrayList<MethodParameter>();

            Annotation[][] annotations = method.getParameterAnnotations();
            Type[] genericTypes = method.getGenericParameterTypes();
            Class<?>[] types = method.getParameterTypes();

            int length = types.length;
            for (int i = 0; i < length; i++) {
                Element element = new Element(annotations[i], genericTypes[i], types[i], null);
                MethodParameter parameter = new MethodParameter(element, method, i);
                parameters.add(parameter);
            }

            return new MethodMetadata(method, parameters);
        }

    }

}
