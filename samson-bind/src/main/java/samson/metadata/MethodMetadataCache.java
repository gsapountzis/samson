package samson.metadata;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MethodMetadataCache {

    private final ConcurrentMap<Method, MethodMetadata> cache = new ConcurrentHashMap<Method, MethodMetadata>();

    public MethodMetadata get(final Method method) {

        MethodMetadata metadata = cache.get(method);
        if (metadata == null) {
            final MethodMetadata newMetadata = MethodIntrospector.introspect(method);
            metadata = cache.putIfAbsent(method, newMetadata);
            if (metadata == null) {
                metadata = newMetadata;
            }
        }

        return metadata;
    }

}
