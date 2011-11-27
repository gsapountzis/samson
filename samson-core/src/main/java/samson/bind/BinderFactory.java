package samson.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import samson.convert.Converter;
import samson.convert.ConverterProvider;
import samson.convert.MultivaluedTypePredicate;
import samson.jersey.core.reflection.ReflectionHelper;
import samson.metadata.BeanTcp;
import samson.metadata.BeanTcpCache;
import samson.metadata.ElementRef;
import samson.metadata.TypeClassPair;

public class BinderFactory {

    private static final Logger LOGGER = Logger.getLogger(BinderFactory.class.getName());

    private final BeanTcpCache beanCache;
    private ConverterProvider converterProvider;
    private MultivaluedTypePredicate stringTypePredicate;

    public BinderFactory() {
        this.beanCache = new BeanTcpCache();
    }

    public void setConverterProvider(ConverterProvider converterProvider) {
        this.converterProvider = converterProvider;
    }

    public void setStringTypePredicate(MultivaluedTypePredicate stringTypePredicate) {
        this.stringTypePredicate = stringTypePredicate;
    }

    public BeanTcp getBeanMetadata(TypeClassPair tcp) {
        return beanCache.get(tcp);
    }

    public Converter<?> getConverter(TypeClassPair tcp, Annotation annotations[]) {
        return converterProvider.get(tcp.c, tcp.t, annotations);
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
                        LOGGER.log(Level.WARNING, "Composite type " + tcp.c + " does not have a no-arg constructor");
                        type = BinderType.NULL;
                    }
                }
            }
        }
        else {
            boolean isStringType = stringTypePredicate.apply(tcp.c, tcp.t, null);
            if (!isStringType) {
                LOGGER.log(Level.WARNING, "String-based type " + tcp.c + " does not have an extractor");
                type = BinderType.NULL;
            }
        }

        return type;
    }

}
