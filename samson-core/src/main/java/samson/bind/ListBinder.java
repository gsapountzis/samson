package samson.bind;

import java.lang.annotation.Annotation;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.Element;
import samson.JForm;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;
import samson.metadata.ListTcp;

class ListBinder extends Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListBinder.class);

    ListBinder(BinderFactory factory, ElementRef ref) {
        super(factory, BinderType.LIST, ref);
    }

    /**
     * Bind list parameters, i.e. indexed parameters.
     */
    @Override
    public void read(BinderNode<?> node) {
        Annotation[] annotations = ref.element.annotations;
        ListTcp listTcp = new ListTcp(ref.element.tcp);
        List<?> list = (List<?>) ref.accessor.get();
        if (list == null) {
            list = listTcp.createInstance();
            ref.accessor.set(list);
        }

        for (BinderNode<?> child : node.getChildren()) {
            String stringIndex = child.getName();

            ElementRef childRef = getElementRef(annotations, listTcp, list, stringIndex);
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
    public ElementRef getElementRef(String name) {
        Annotation[] annotations = ref.element.annotations;
        ListTcp listTcp = new ListTcp(ref.element.tcp);
        List<?> list = (List<?>) ref.accessor.get();

        return getElementRef(annotations, listTcp, list, name);
    }

    private ElementRef getElementRef(Annotation[] annotations, ListTcp listTcp, List<?> list, String stringIndex) {
        int index = getIndex(stringIndex);
        if (index >= 0 && index < JForm.CONF_MAX_LIST_SIZE) {
            Element itemElement = listTcp.createItemElement(annotations, stringIndex);
            ElementAccessor itemAccessor = ListTcp.createItemAccessor(list, index);
            return new ElementRef(itemElement, itemAccessor);
        }
        else {
            LOGGER.warn("Invalid list index: {}", stringIndex);
            return ElementRef.NULL_REF;
        }
    }

    private int getIndex(String stringIndex) {
        try {
            return Integer.parseInt(stringIndex);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

}
