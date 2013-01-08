package samson.bind;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.BeanMetadata;
import samson.metadata.BeanProperty;
import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.TypeClassPair;

class BeanBinder extends Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanBinder.class);

    private final BeanMetadata metadata;

    BeanBinder(BinderFactory factory, Element element) {
        super(factory, element);
        this.metadata = factory.getBeanMetadata(element.tcp);
    }

    @Override
    public TypedNode child(String name, Object object) {
        Object bean = object;
        BeanProperty property = metadata.getProperty(name);
        if (property != null) {
            ElementAccessor accessor = createAccessor(bean, property);
            return new AnyNode(property, accessor.get());
        }
        else {
            LOGGER.warn("Invalid bean property: {}", name);
            return NullNode.INSTANCE;
        }
    }

    @Override
    public TypedNode parse(UntypedNode untypedNode, Object object) {
        Object bean = object;
        if (bean == null) {
            bean = createBean(element.tcp);
        }

        Map<String,TypedNode> nodes = new LinkedHashMap<String,TypedNode>();

        for (Entry<String, UntypedNode> e : untypedNode.getChildren().entrySet()) {
            String name = e.getKey();
            UntypedNode untypedChild = e.getValue();

            BeanProperty property = metadata.getProperty(name);
            if (property != null) {
                ElementAccessor accessor = createAccessor(bean, property);

                Binder binder = factory.getBinder(property, untypedChild.hasChildren());
                TypedNode child = binder.parse(untypedChild, accessor.get());

                accessor.set(child.getObject());
                nodes.put(name, child);
            }
            else {
                LOGGER.warn("Invalid bean property: {}", name);
            }
        }

        return new BeanNode(element, bean, nodes);
    }

    public static Object createBean(TypeClassPair tcp) {
        Class<?> beanClass = tcp.c;

        try {
            return beanClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ElementAccessor createAccessor(final Object bean, final BeanProperty property) {
        if (bean == null) {
            return ElementAccessor.NULL_ACCESSOR;
        }

        return new ElementAccessor() {

            @Override
            public void set(Object value) {
                property.set(bean, value);
            }

            @Override
            public Object get() {
                return property.get(bean);
            }
        };
    }

}
