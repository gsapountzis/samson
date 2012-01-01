package samson.bind;

import static samson.Configuration.MAX_LIST_SIZE;

import java.lang.annotation.Annotation;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.Element;
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
            ElementRef childRef = getChildRef(annotations, listTcp, list, stringIndex);

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            binder.read(child);
            child.setBinder(binder);
        }
    }

    @Override
    public ElementRef getChildRef(String name) {
        Annotation[] annotations = ref.element.annotations;
        ListTcp listTcp = new ListTcp(ref.element.tcp);
        List<?> list = (List<?>) ref.accessor.get();

        ElementRef childRef = getChildRef(annotations, listTcp, list, name);
        return childRef;
    }

    private ElementRef getChildRef(Annotation[] annotations, ListTcp listTcp, List<?> list, String stringIndex) {
        int index = getIndex(stringIndex);
        if (index >= 0 && index < MAX_LIST_SIZE) {
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
