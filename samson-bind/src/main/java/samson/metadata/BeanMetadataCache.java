package samson.metadata;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BeanMetadataCache {

    private final ConcurrentMap<Class<?>, BeanMetadata> cache = new ConcurrentHashMap<Class<?>, BeanMetadata>();

    public BeanMetadata get(final TypeClassPair tcp) {
        if (tcp == null) {
            return null;
        }

        final Class<?> clazz = tcp.c;

        BeanMetadata metadata = cache.get(clazz);
        if (metadata == null) {
            final BeanMetadata newBean = BeanIntrospector.introspect(tcp);
            metadata = cache.putIfAbsent(clazz, newBean);
            if (metadata == null) {
                metadata = newBean;
            }
        }

        return metadata;
    }

}
