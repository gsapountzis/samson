package samson.metadata;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import samson.jersey.spi.inject.Errors;
import samson.jersey.spi.inject.Errors.Closure;

public class BeanMetadataCache {

    private final ConcurrentMap<Class<?>, BeanMetadata> cache = new ConcurrentHashMap<Class<?>, BeanMetadata>();

    public BeanMetadata get(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }

        BeanMetadata bean = cache.get(clazz);
        if (bean == null) {
            final BeanMetadata newBean = Errors.processWithErrors(new Closure<BeanMetadata>() {
                @Override
                public BeanMetadata f() {
                    return BeanIntrospector.createBeanMetadata(clazz);
                }
            });

            bean = cache.putIfAbsent(clazz, newBean);
            if (bean == null) {
                bean = newBean;
            }
        }

        return bean;
    }

}
