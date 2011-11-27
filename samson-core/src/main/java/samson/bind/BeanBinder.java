package samson.bind;

import samson.metadata.BeanProperty;
import samson.metadata.BeanTcp;
import samson.metadata.Element.Accessor;
import samson.metadata.ElementRef;

class BeanBinder extends Binder {

    BeanBinder(BinderFactory factory, ElementRef ref) {
        super(BinderType.BEAN, factory, ref);
    }

    @Override
    public void read(ParamNode<?> beanTree) {
        BeanTcp beanTcp = factory.getBeanMetadata(ref.element.tcp);

        Object bean = ref.accessor.get();
        if (bean == null) {
            bean = BeanTcp.createInstance(beanTcp.getTcp());
            ref.accessor.set(bean);
        }

        for (BeanProperty property : beanTcp.getProperties().values()) {
            if (!beanTree.hasChild(property.name))
                continue;

            ParamNode<?> propertyTree = beanTree.getChild(property.name);
            ElementRef propertyRef = getElementRef(bean, property);

            Binder binder = factory.getBinder(propertyRef, propertyTree.hasChildren());
            if (binder != Binder.NULL_BINDER) {
                binder.read(propertyTree);
                node.addChild(binder.getNode());
            }
        }
    }

    @Override
    public ElementRef getElementRef(String name) {
        BeanTcp beanTcp = factory.getBeanMetadata(ref.element.tcp);

        Object bean = ref.accessor.get();

        if (beanTcp.hasProperty(name)) {
            BeanProperty property = beanTcp.getProperty(name);
            return getElementRef(bean, property);
        }
        else {
            return ElementRef.NULL_REF;
        }
    }

    private ElementRef getElementRef(Object bean, BeanProperty property) {
        Accessor propertyAccessor = BeanTcp.createAccessor(bean, property);
        return new ElementRef(property, propertyAccessor);
    }

}
