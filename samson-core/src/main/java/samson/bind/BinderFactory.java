package samson.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.convert.Converter;
import samson.convert.ConverterProvider;
import samson.jersey.core.reflection.ReflectionHelper;
import samson.metadata.BeanMetadata;
import samson.metadata.BeanMetadataCache;
import samson.metadata.BeanTcp;
import samson.metadata.ElementRef;
import samson.metadata.TypeClassPair;

public class BinderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinderFactory.class);

    private final BeanMetadataCache beanCache;
    private final ConverterProvider converterProvider;

    public BinderFactory(ConverterProvider converterProvider) {
        this.beanCache = new BeanMetadataCache();
        this.converterProvider = converterProvider;
    }

    public BeanTcp getBeanMetadata(TypeClassPair tcp) {
        BeanMetadata metadata = beanCache.get(tcp.c);
        return new BeanTcp(tcp, metadata.getProperties());
    }

    public Converter<?> getConverter(TypeClassPair tcp, Annotation annotations[]) {
        return converterProvider.get(tcp.t, tcp.c, annotations);
    }

    public Binder getBinder(ElementRef ref, boolean composite) {
        BinderType type = getType(ref.element.tcp, composite);

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

    public BinderType getType(TypeClassPair tcp, boolean composite) {

        Class<?> clazz = tcp.c;
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

        if (composite) {
            // we require no-arg constructor for non-abstract beans
            if (type == BinderType.BEAN) {
                final int modifiers = clazz.getModifiers();
                if (!Modifier.isAbstract(modifiers)) {
                    Constructor<?> constructor = ReflectionHelper.getNoargConstructor(clazz);
                    if (constructor == null) {
                        LOGGER.warn("Composite type {} does not have a no-arg constructor", tcp.c);
                        type = BinderType.NULL;
                    }
                }
            }
        }
        else {
            boolean isStringType = converterProvider.canConvert(tcp.t, tcp.c, null);
            if (!isStringType) {
                LOGGER.warn("String-based type {} does not have an extractor", tcp.c);
                type = BinderType.NULL;
            }
        }

        return type;
    }

}
