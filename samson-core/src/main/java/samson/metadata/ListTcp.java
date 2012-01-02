package samson.metadata;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import samson.jersey.core.reflection.ReflectionHelper;

public class ListTcp {

    private final Annotation[] itemAnnotations;

    private final TypeClassPair tcp;
    private final TypeClassPair itemTcp;

    public ListTcp(Element element) {
        this.itemAnnotations = component(element.annotations);

        this.tcp = element.tcp;
        this.itemTcp = ReflectionHelper.getTypeArgumentAndClass(tcp.t);

        if (itemTcp == null) {
            throw new IllegalArgumentException("Parameterized type without type arguement");
        }
    }

    public TypeClassPair getTcp() {
        return tcp;
    }

    public TypeClassPair getItemTcp() {
        return itemTcp;
    }

    public List<?> createInstance() {
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

    public Element createItemElement(String index) {
        return new Element(itemAnnotations, itemTcp, index);
    }

    public static ElementAccessor createItemAccessor(final List<?> list, final int index) {
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

    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    /**
     * Filter annotations placed on the composite (list / map) that apply to the
     * component (list element / map entry).
     * <p>
     * We could use an annotation whose value is another annotation e.g.
     * &#064;Component(&#064;NotEmpty).
     */
    static Annotation[] component(Annotation[] composite) {
        return EMPTY_ANNOTATIONS;
    }
}
