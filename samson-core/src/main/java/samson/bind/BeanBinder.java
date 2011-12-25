package samson.bind;

import samson.metadata.BeanProperty;
import samson.metadata.BeanTcp;
import samson.metadata.ElementRef;

class BeanBinder extends Binder {

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

        for (BeanProperty property : beanTcp.getProperties().values()) {
            BinderNode<?> child = node.getChild(property.name);
            if (child == null)
                continue;

            ElementRef childRef = new ElementRef(property, property.createAccessor(bean));

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            if (binder != Binder.NULL_BINDER) {
                binder.read(child);
                child.setBinder(binder);
            }
        }
    }

    @Override
    public ElementRef readChildRef(String childName) {
        BeanTcp beanTcp = factory.getBeanTcp(ref.element.tcp);
        Object bean = ref.accessor.get();

        if (beanTcp.hasProperty(childName)) {
            BeanProperty property = beanTcp.getProperty(childName);

            ElementRef childRef = new ElementRef(property, property.createAccessor(bean));

            return childRef;
        }
        else {
            return ElementRef.NULL_REF;
        }
    }

}
