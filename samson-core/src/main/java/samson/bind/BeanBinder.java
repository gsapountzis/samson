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

            ElementRef childRef = getElementRef(beanTcp, bean, propertyName);
            if (childRef == ElementRef.NULL_REF)
                continue;

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            if (binder != Binder.NULL_BINDER) {
                binder.read(child);
                child.setBinder(binder);
            }
        }
    }

    @Override
    public void readComposite(BinderNode<?> node) {
        BeanTcp beanTcp = factory.getBeanTcp(ref.element.tcp);
        Object bean = ref.accessor.get();

        for (BinderNode<?> child : node.getChildren()) {
            String propertyName = child.getName();

            ElementRef childRef = getElementRef(beanTcp, bean, propertyName);
            if (childRef == ElementRef.NULL_REF)
                continue;

            if (child.hasChildren()) {
                Binder binder = factory.getBinder(childRef, true);
                if (binder != Binder.NULL_BINDER) {
                    binder.readComposite(child);
                    child.setBinder(binder);
                }
            }
            else {
                Binder binder = new StringBinder(childRef);
                child.setBinder(binder);
            }
        }
    }

    private ElementRef getElementRef(BeanTcp beanTcp, Object bean, String propertyName) {
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
