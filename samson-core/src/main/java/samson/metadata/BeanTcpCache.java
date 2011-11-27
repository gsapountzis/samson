package samson.metadata;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import samson.jersey.spi.inject.Errors;

public class BeanTcpCache {

    private final ConcurrentMap<Class<?>, BeanTcp> beanMap = new ConcurrentHashMap<Class<?>, BeanTcp>();

    public BeanTcp get(final TypeClassPair tcp) {
        Class<?> clazz = tcp.c;
        if (clazz == null) {
            return null;
        }

        BeanTcp bean = beanMap.get(clazz);
        if (bean == null) {
            final BeanTcp newBean = Errors.processWithErrors(new Errors.Closure<BeanTcp>() {
                @Override
                public BeanTcp f() {
                    return BeanIntrospector.createBeanMetadata(tcp);
                }
            });

            bean = beanMap.putIfAbsent(clazz, newBean);
            if (bean == null) {
                bean = newBean;
            }
        }

        return bean;
    }

}
