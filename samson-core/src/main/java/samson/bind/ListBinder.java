package samson.bind;

import static samson.Configuration.MAX_LIST_SIZE;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;
import samson.metadata.ListItem;
import samson.metadata.TypeClassPair;

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
        ListItem listItem = ListItem.fromList(ref.element);
        List<?> list = (List<?>) ref.accessor.get();
        if (list == null) {
            list = createInstance(ref.element.tcp);
            ref.accessor.set(list);
        }

        for (BinderNode<?> child : node.getChildren()) {
            String stringIndex = child.getName();
            ElementRef childRef = getChildRef(listItem, list, stringIndex);

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            binder.read(child);
            child.setBinder(binder);
        }
    }

    @Override
    public ElementRef getChildRef(String name) {
        ListItem listItem = ListItem.fromList(ref.element);
        List<?> list = (List<?>) ref.accessor.get();

        ElementRef childRef = getChildRef(listItem, list, name);
        return childRef;
    }

    private ElementRef getChildRef(ListItem listItem, List<?> list, String stringIndex) {
        int index = getIndex(stringIndex);
        if (index >= 0 && index < MAX_LIST_SIZE) {
            Element itemElement = listItem.createElement(stringIndex);
            ElementAccessor itemAccessor = ListItem.createAccessor(list, index);
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

    public static List<?> createInstance(TypeClassPair tcp) {
        Class<?> listClass = tcp.c;

        if (!List.class.isAssignableFrom(listClass)) {
            throw new IllegalArgumentException();
        }

        if (listClass.isInterface()) {
            if (listClass == List.class) {
                listClass = ArrayList.class;
            }
            else {
                throw new RuntimeException("Unknown list interface");
            }
        }

        try {
            return (List<?>) listClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

}
