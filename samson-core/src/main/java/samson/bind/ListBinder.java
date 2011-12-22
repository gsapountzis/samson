package samson.bind;

import java.lang.annotation.Annotation;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.Element;
import samson.metadata.Element.Accessor;
import samson.metadata.ElementRef;
import samson.metadata.ListTcp;

class ListBinder extends Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ListBinder.class);

    /**
     * Maximum list size.
     * <p>
     * This is to prevent DoS attacks. For example the attacker could just set
     * the list index to (2<sup>32</sup> - 1) and cause the allocation of more
     * than 4GB of memory.
     */
    static final int MAX_LIST_SIZE = 256;

    ListBinder(BinderFactory factory, ElementRef ref) {
        super(BinderType.LIST, factory, ref);
    }

    /**
     * Bind list parameters, i.e. indexed parameters.
     */
    @Override
    public void read(ParamNode<?> listTree) {
        Annotation[] annotations = ref.element.annotations;
        ListTcp listTcp = new ListTcp(ref.element.tcp);

        List<?> list = (List<?>) ref.accessor.get();
        if (list == null) {
            list = listTcp.createInstance();
            ref.accessor.set(list);
        }

        for (ParamNode<?> itemTree : listTree.getChildren()) {
            String stringIndex = itemTree.getName();

            ElementRef itemRef = getElementRef(annotations, listTcp, list, stringIndex);
            if (itemRef == ElementRef.NULL_REF)
                continue;

            Binder binder = factory.getBinder(itemRef, itemTree.hasChildren());
            if (binder != Binder.NULL_BINDER) {
                binder.read(itemTree);
                node.addChild(binder.getNode());
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
        if (index >= 0 && index < MAX_LIST_SIZE) {
            Element itemElement = listTcp.createItemElement(annotations, stringIndex);
            Accessor itemAccessor = ListTcp.createItemAccessor(list, index);
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
