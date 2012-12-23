package samson.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.BeanMetadata;
import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;
import samson.metadata.TypeClassPair;

class BeanBinder extends Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanBinder.class);

    BeanBinder(BinderFactory factory, ElementRef ref) {
        super(factory, BinderType.BEAN, ref);
    }

    @Override
    public void read(BinderNode<?> node) {
        BeanMetadata metadata = factory.getBeanMetadata(ref.element.tcp);
        Object bean = ref.accessor.get();
        if (bean == null) {
            bean = createInstance(ref.element.tcp);
            ref.accessor.set(bean);
        }

        for (BinderNode<?> child : node.getChildren()) {
            String propertyName = child.getName();
            ElementRef childRef = getChildRef(metadata, bean, propertyName);
            child.setRef(childRef);

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            binder.read(child);
        }
    }

    @Override
    public ElementRef getChildRef(String name) {
        BeanMetadata metadata = factory.getBeanMetadata(ref.element.tcp);
        Object bean = ref.accessor.get();

        ElementRef childRef = getChildRef(metadata, bean, name);
        return childRef;
    }

    private ElementRef getChildRef(BeanMetadata metadata, Object bean, String propertyName) {
        if (metadata.hasProperty(propertyName)) {
            Element property = metadata.getProperty(propertyName);
            ElementAccessor accessor = metadata.createAccessor(bean, propertyName);
            return new ElementRef(property, accessor);
        }
        else {
            LOGGER.warn("Invalid property name: {}", propertyName);
            return ElementRef.NULL_REF;
        }
    }

    public static Object createInstance(TypeClassPair tcp) {
        Class<?> beanClass = tcp.c;

        try {
            return beanClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

}
