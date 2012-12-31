package samson.bind;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;
import samson.metadata.ResolvedListType;
import samson.metadata.TypeClassPair;

class ListBinder extends Binder {

    /**
     * Maximum list size
     * <p>
     * This is to prevent DoS attacks. For example the attacker could just set
     * the list index to (2<sup>32</sup> - 1) and cause the allocation of more
     * than 4GB of memory.
     */
    private static final int MAX_LIST_SIZE = 256;

    private static final Logger LOGGER = LoggerFactory.getLogger(ListBinder.class);

    ListBinder(BinderFactory factory, ElementRef ref) {
        super(factory, BinderType.LIST, ref);
    }

    /**
     * Bind list parameters, i.e. indexed parameters.
     */
    @Override
    public void read(BinderNode<?> node) {
        ResolvedListType type = new ResolvedListType(ref.element.tcp);
        List<?> list = (List<?>) ref.accessor.get();
        if (list == null) {
            list = createInstance(ref.element.tcp);
            ref.accessor.set(list);
        }

        for (BinderNode<?> child : node.getChildren()) {
            String stringIndex = child.getName();
            ElementRef childRef = getChildRef(type, list, stringIndex);
            child.setRef(childRef);

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            binder.read(child);
        }
    }

    @Override
    public ElementRef getChildRef(String name) {
        ResolvedListType type = new ResolvedListType(ref.element.tcp);
        List<?> list = (List<?>) ref.accessor.get();

        ElementRef childRef = getChildRef(type, list, name);
        return childRef;
    }

    private ElementRef getChildRef(ResolvedListType type, List<?> list, String stringIndex) {
        int index = getIndex(stringIndex);
        if (index >= 0 && index < MAX_LIST_SIZE) {
            Element itemElement = type.getItem();
            ElementAccessor itemAccessor = createAccessor(list, index);
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

    public static Set<?> createSet(TypeClassPair tcp) {
        Class<?> setClass = tcp.c;

        if (!Set.class.isAssignableFrom(setClass)) {
            throw new IllegalArgumentException();
        }

        if (setClass.isInterface()) {
            if (setClass == Set.class) {
                setClass = LinkedHashSet.class;
            }
            else if (setClass == SortedSet.class) {
                setClass = TreeSet.class;
            }
            else {
                throw new RuntimeException("Unknown set interface");
            }
        }

        try {
            return (Set<?>) setClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
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

    public static ElementAccessor createAccessor(final List<?> list, final int index) {
        if (list == null) {
            return ElementAccessor.NULL_ACCESSOR;
        }

        return new ElementAccessor() {

            @SuppressWarnings("unchecked")
            @Override
            public void set(Object value) {
                for (int i = list.size(); i <= index; i++) { list.add(null); }
                ((List<Object>) list).set(index, value);
            }

            @Override
            public Object get() {
                for (int i = list.size(); i <= index; i++) { list.add(null); }
                return list.get(index);
            }
        };
    }

}
