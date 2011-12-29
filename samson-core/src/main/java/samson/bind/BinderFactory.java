package samson.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.TypeClassPair;
import samson.convert.Converter;
import samson.convert.ConverterProvider;
import samson.jersey.core.reflection.ReflectionHelper;
import samson.metadata.BeanMetadata;
import samson.metadata.BeanMetadataCache;
import samson.metadata.BeanTcp;
import samson.metadata.ElementRef;

public class BinderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinderFactory.class);

    private final BeanMetadataCache beanCache;
    private final ConverterProvider converterProvider;

    public BinderFactory(ConverterProvider converterProvider) {
        this.beanCache = new BeanMetadataCache();
        this.converterProvider = converterProvider;
    }

    public BeanTcp getBeanTcp(TypeClassPair tcp) {
        BeanMetadata metadata = beanCache.get(tcp.c);
        return new BeanTcp(tcp, metadata.getProperties());
    }

    public Converter<?> getConverter(TypeClassPair tcp, Annotation annotations[]) {
        return converterProvider.get(tcp.t, tcp.c, annotations);
    }

    public Binder getBinder(ElementRef ref, boolean composite) {
        return getBinder(ref, composite, true);
    }

    public Binder getBinder(ElementRef ref, boolean composite, boolean validate) {
        BinderType type = getBinderType(ref.element.tcp, composite);

        if (validate) {
            type = validateBinderType(type, ref.element.tcp, ref.accessor.get(), composite);
        }

        if (type == BinderType.STRING) {
            return new StringBinder(ref);
        }
        else if (type == BinderType.LIST) {
            return new ListBinder(this, ref);
        }
        else if (type == BinderType.MAP) {
            return new MapBinder(this, ref);
        }
        else if (type == BinderType.BEAN) {
            return new BeanBinder(this, ref);
        }
        else {
            return Binder.NULL_BINDER;
        }
    }

    private BinderType getBinderType(TypeClassPair tcp, boolean composite) {
        if (tcp == null) {
            return BinderType.NULL;
        }

        final Class<?> clazz = tcp.c;

        BinderType type = BinderType.NULL;
        if (composite) {
            if (List.class.isAssignableFrom(clazz)) {
                type = BinderType.LIST;
            }
            else if (Map.class.isAssignableFrom(clazz)) {
                type = BinderType.MAP;
            }
            else {
                type = BinderType.BEAN;
            }
        }
        else {
            type = BinderType.STRING;
        }

        return type;
    }

    private BinderType validateBinderType(BinderType type, TypeClassPair tcp, Object instance, boolean composite) {
        if (tcp == null) {
            return BinderType.NULL;
        }

        final Class<?> clazz = tcp.c;

        if (composite) {
            if (type == BinderType.BEAN) {
                final int modifiers = clazz.getModifiers();
                if (Modifier.isAbstract(modifiers)) {
                    if (instance == null) {
                        LOGGER.warn("{} cannot be instantiated", clazz);
                        type = BinderType.NULL;
                    }
                }
                else {
                    // we require no-arg constructor for non-abstract beans
                    Constructor<?> constructor = ReflectionHelper.getNoargConstructor(clazz);
                    if (constructor == null) {
                        LOGGER.warn("{} does not have a no-arg constructor", clazz);
                        type = BinderType.NULL;
                    }
                }
            }
        }
        else {
            boolean isStringType = converterProvider.isConvertible(tcp.t, tcp.c);
            if (!isStringType) {
                LOGGER.warn("{} cannot be converted", clazz);
                type = BinderType.NULL;
            }
        }

        return type;
    }

}
