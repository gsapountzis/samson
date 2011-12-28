package samson.bind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.BeanProperty;
import samson.metadata.BeanTcp;
import samson.metadata.ElementRef;

class BeanBinder extends Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanBinder.class);

    BeanBinder(BinderFactory factory, ElementRef ref) {
        super(factory, BinderType.BEAN, ref);
    }

    @Override
    public void read(BinderNode<?> node) {
        BeanTcp beanTcp = factory.getBeanTcp(ref.element.tcp);
        Object bean = ref.accessor.get();

        if (bean == null) {
            bean = beanTcp.createInstance();
            ref.accessor.set(bean);
        }

        for (BinderNode<?> child : node.getChildren()) {
            String propertyName = child.getName();
            ElementRef childRef = getChildRef(beanTcp, bean, propertyName);

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            binder.read(child);
            child.setBinder(binder);
        }
    }

    @Override
    public ElementRef getChildRef(String name) {
        BeanTcp beanTcp = factory.getBeanTcp(ref.element.tcp);
        Object bean = ref.accessor.get();

        ElementRef childRef = getChildRef(beanTcp, bean, name);
        return childRef;
    }

    private ElementRef getChildRef(BeanTcp beanTcp, Object bean, String propertyName) {
        if (beanTcp.hasProperty(propertyName)) {
            BeanProperty property = beanTcp.getProperty(propertyName);
            return new ElementRef(property, property.createAccessor(bean));
        }
        else {
            LOGGER.warn("Invalid property name: {}", propertyName);
            return ElementRef.NULL_REF;
        }
    }

}
