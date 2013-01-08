package samson.bind;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.Element;
import samson.metadata.ElementAccessor;
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

    private final ResolvedListType type;

    ListBinder(BinderFactory factory, Element element) {
        super(factory, element);
        this.type = new ResolvedListType(element.tcp);
    }

    @Override
    public TypedNode child(String name, Object object) {
        List<?> list = (List<?>) object;
        int index = getIndex(name);
        if (index >= 0 && index < MAX_LIST_SIZE) {
            ElementAccessor accessor = createAccessor(list, index);
            return new AnyNode(type.getItem(), accessor.get());
        }
        else {
            LOGGER.warn("Invalid list index: {}", name);
            return NullNode.INSTANCE;
        }
    }

    /**
     * Bind list parameters, i.e. indexed parameters.
     */
    @Override
    public TypedNode parse(UntypedNode untypedNode, Object object) {
        List<?> list = (List<?>) object;
        if (list == null) {
            list = createList(element.tcp);
        }

        List<TypedNode> nodes = new ArrayList<TypedNode>();

        for (Entry<String, UntypedNode> e : untypedNode.getChildren().entrySet()) {
            String name = e.getKey();
            UntypedNode untypedChild = e.getValue();

            int index = getIndex(name);
            if (index >= 0 && index < MAX_LIST_SIZE) {
                ElementAccessor accessor = createAccessor(list, index);

                Binder binder = factory.getBinder(type.getItem(), untypedChild.hasChildren());
                TypedNode child = binder.parse(untypedChild, accessor.get());

                accessor.set(child.getObject());
                for (int i = nodes.size(); i <= index; i++) { nodes.add(null); }
                nodes.set(index, child);
            }
            else {
                LOGGER.warn("Invalid list index: {}", name);
            }
        }

        return new ListNode(element, list, nodes);
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

    public static List<?> createList(TypeClassPair tcp) {
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
