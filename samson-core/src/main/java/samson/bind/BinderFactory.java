package samson.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.convert.Converter;
import samson.convert.ConverterException;
import samson.convert.ConverterProvider;
import samson.convert.MultivaluedConverter;
import samson.metadata.BeanMetadata;
import samson.metadata.BeanMetadataCache;
import samson.metadata.Element;
import samson.metadata.ElementRef;
import samson.metadata.ReflectionHelper;
import samson.metadata.TypeClassPair;

public class BinderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinderFactory.class);

    private final BeanMetadataCache beanCache;
    private final ConverterProvider converterProvider;

    public BinderFactory(ConverterProvider converterProvider) {
        this.beanCache = new BeanMetadataCache();
        this.converterProvider = converterProvider;
    }

    public BeanMetadata getBeanMetadata(TypeClassPair tcp) {
        return beanCache.get(tcp);
    }

    public Converter<?> getConverter(TypeClassPair tcp, Annotation annotations[]) {
        return converterProvider.get(tcp.t, tcp.c, annotations);
    }

    public Binder getBinder(ElementRef ref, boolean composite) {
        return getBinder(ref, composite, true);
    }

    public Binder getBinder(ElementRef ref, boolean composite, boolean validate) {

        // check for null element
        if (ref.element.tcp == null) {
            return Binder.NULL_BINDER;
        }

        BinderType type = getBinderType(ref.element.tcp, composite);

        if (validate) {
            type = validateBinderType(type, ref.element.tcp, ref.accessor.get(), composite);
        }

        if (type == BinderType.STRING) {
            return new StringBinder(this, ref);
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
        final Class<?> clazz = tcp.c;

        if (composite) {
            return getCompositeBinderType(clazz);
        }
        else {
            boolean isConvertibleFromString = converterProvider.isConvertible(tcp.t, tcp.c);
            if (isConvertibleFromString) {
                return BinderType.STRING;
            }
            else {
                LOGGER.warn("{} is not convertible from string values, assuming composite type (bean/list/map)", clazz);
                return getCompositeBinderType(clazz);
            }
        }
    }

    private BinderType getCompositeBinderType(Class<?> clazz) {

        if (List.class.isAssignableFrom(clazz)) {
            return BinderType.LIST;
        }
        else if (Map.class.isAssignableFrom(clazz)) {
            return BinderType.MAP;
        }
        else {
            return BinderType.BEAN;
        }
    }

    private BinderType validateBinderType(BinderType type, TypeClassPair tcp, Object instance, boolean composite) {
        final Class<?> clazz = tcp.c;

        if (type == BinderType.BEAN) {
            final int modifiers = clazz.getModifiers();

            if (!Modifier.isPublic(modifiers)) {
                if (instance == null) {
                    LOGGER.warn("{} is not a public class and cannot be instantiated", clazz);
                    type = BinderType.NULL;
                }
            }

            if (Modifier.isAbstract(modifiers)) {
                if (instance == null) {
                    LOGGER.warn("{} is an abstract class or interface and cannot be instantiated", clazz);
                    type = BinderType.NULL;
                }
            }

            if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers)) {
                // we require no-arg constructor for non-abstract beans
                Constructor<?> constructor = ReflectionHelper.getNoargConstructor(clazz);
                if (constructor == null) {
                    LOGGER.warn("{} does not have a no-arg constructor and cannot be instantiated", clazz);
                    type = BinderType.NULL;
                }
            }
        }

        return type;
    }

    public static Object createInstanceIfComposite(Binder binder) {
        BinderType type = binder.type;
        ElementRef ref = binder.ref;
        TypeClassPair tcp = ref.element.tcp;

        if (type == BinderType.LIST) {
            return ListBinder.createInstance(tcp);
        }
        else if (type == BinderType.MAP) {
            return MapBinder.createInstance(tcp);
        }
        else if (type == BinderType.BEAN) {
            return BeanBinder.createInstance(tcp);
        }
        else {
            return null;
        }
    }

    public ConversionResult fromStringList(Element element, List<String> values) {

        // check for null element
        if (element.tcp == null) {
            return null;
        }

        MultivaluedConverter<?> extractor = converterProvider.getMultivalued(
                element.tcp.t,
                element.tcp.c,
                element.annotations,
                element.jaxrs.encoded,
                element.jaxrs.defaultValue);

        if (extractor != null) {
            try {
                Object value = extractor.fromStringList(values);
                return ConversionResult.fromValue(value);
            }
            catch (ConverterException ex) {
                return ConversionResult.fromError(ex);
            }
        }
        else {
            return null;
        }
    }

    public List<String> toStringList(Element element, Object value) {

        // check for null element
        if (element.tcp == null) {
            return Collections.emptyList();
        }

        @SuppressWarnings("unchecked")
        MultivaluedConverter<Object> extractor = (MultivaluedConverter<Object>) converterProvider.getMultivalued(
                element.tcp.t,
                element.tcp.c,
                element.annotations);

        if (extractor != null) {
            return extractor.toStringList(value);
        }
        else {
            return Collections.emptyList();
        }
    }

}
