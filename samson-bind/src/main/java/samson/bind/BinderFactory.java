package samson.bind;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.convert.Converter;
import samson.convert.ConverterException;
import samson.convert.ConverterProvider;
import samson.convert.multivalued.MultivaluedConverter;
import samson.convert.multivalued.MultivaluedConverterProvider;
import samson.metadata.BeanMetadata;
import samson.metadata.BeanMetadataCache;
import samson.metadata.Element;
import samson.metadata.TypeClassPair;

import com.google.common.base.Preconditions;

public class BinderFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinderFactory.class);

    private final BeanMetadataCache beanCache;
    private final ConverterProvider converterProvider;
    private final MultivaluedConverterProvider multivaluedConverterProvider;

    public BinderFactory(ConverterProvider converterProvider, MultivaluedConverterProvider multivaluedConverterProvider) {
        this.beanCache = new BeanMetadataCache();
        this.converterProvider = converterProvider;
        this.multivaluedConverterProvider = multivaluedConverterProvider;
    }

    public BeanMetadata getBeanMetadata(TypeClassPair tcp) {
        return beanCache.get(tcp);
    }

    public Converter<?> getConverter(TypeClassPair tcp, Annotation annotations[]) {
        return converterProvider.get(tcp.c, tcp.t, annotations);
    }

    public Binder getBinder(Element element, boolean composite) {
        NodeType type = getNodeType(element.tcp, composite);

        if (type == NodeType.LIST) {
            return new ListBinder(this, element);
        }
        else if (type == NodeType.MAP) {
            return new MapBinder(this, element);
        }
        else if (type == NodeType.BEAN) {
            return new BeanBinder(this, element);
        }
        else if (type == NodeType.VALUE) {
            return new ValueBinder(this, element);
        }
        else if (type == NodeType.NULL) {
            return Binder.NULL_BINDER;
        }
        else {
            throw new IllegalStateException();
        }
    }

    private NodeType getNodeType(TypeClassPair tcp, boolean composite) {

        // check for null element
        if (tcp == null) {
            return NodeType.NULL;
        }

        if (composite) {
            return getCompositeType(tcp);
        }
        else {
            if (isValidValueType(tcp)) {
                return NodeType.VALUE;
            }
            else {
                return getCompositeType(tcp);
            }
        }
    }

    private NodeType getCompositeType(TypeClassPair tcp) {

        if (List.class.isAssignableFrom(tcp.c)) {
            return NodeType.LIST;
        }
        else if (Map.class.isAssignableFrom(tcp.c)) {
            return NodeType.MAP;
        }
        else {
            if (isValidBeanType(tcp)) {
                return NodeType.BEAN;
            }
            else {
                throw new RuntimeException("Invalid bean type");
            }
        }
    }

    private boolean isValidBeanType(TypeClassPair tcp) {
        final int modifiers = tcp.c.getModifiers();

        if (!Modifier.isPublic(modifiers)) {
            LOGGER.warn("{} is not a public class and cannot be instantiated", tcp.c);
            return false;
        }

        if (Modifier.isAbstract(modifiers)) {
            LOGGER.warn("{} is an abstract class or interface and cannot be instantiated", tcp.c);
            return false;
        }

        if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers)) {
            // we require no-arg constructor for non-abstract beans
            try {
                tcp.c.getDeclaredConstructor();
            }
            catch (Exception e) {
                LOGGER.warn("{} does not have a no-arg constructor and cannot be instantiated", tcp.c);
                return false;
            }
        }

        BeanMetadata metadata = beanCache.get(tcp);
        if (metadata.getProperties().isEmpty()) {
            LOGGER.warn("{} does not have any bean properties", tcp.c);
            return false;
        }

        return true;
    }

    private boolean isValidValueType(TypeClassPair tcp) {
        MultivaluedConverter<?> extractor = multivaluedConverterProvider.getMultivalued(tcp.c, tcp.t, Element.NO_ANNOTATIONS);

        if (extractor == null) {
            LOGGER.warn("{} is not convertible from string values", tcp.c);
            return false;
        }

        return true;
    }

    public ConversionResult fromStringList(Element element, List<String> values) {

        // check for null element
        Preconditions.checkNotNull(element.tcp);

        MultivaluedConverter<?> extractor = multivaluedConverterProvider.getMultivalued(
                element.tcp.c,
                element.tcp.t,
                element.annotations,
                element.jaxrs.encoded,
                element.jaxrs.defaultValue);

        Preconditions.checkNotNull(extractor);

        try {
            Object value = extractor.fromStringList(values);
            return ConversionResult.fromValue(value);
        }
        catch (ConverterException ex) {
            return ConversionResult.fromError(ex);
        }
    }

    public List<String> toStringList(Element element, Object value) {

        // check for null element
        Preconditions.checkNotNull(element.tcp);

        @SuppressWarnings("unchecked")
        MultivaluedConverter<Object> extractor = (MultivaluedConverter<Object>) multivaluedConverterProvider.getMultivalued(
                element.tcp.c,
                element.tcp.t,
                element.annotations);

        Preconditions.checkNotNull(extractor);

        return extractor.toStringList(value);
    }

}
