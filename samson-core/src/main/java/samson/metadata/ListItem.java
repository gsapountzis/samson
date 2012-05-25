package samson.metadata;

import java.lang.annotation.Annotation;
import java.util.List;

public class ListItem extends Element {

    public final int index;

    private ListItem(Annotation[] annotations, TypeClassPair tcp, int index) {
        super(annotations, tcp);
        this.index = index;
    }

    public static ListItem fromList(Element list) {
        Annotation[] annotations = component(list.annotations);

        TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(list.tcp.t);
        if (tcp == null) {
            throw new IllegalArgumentException("Parameterized type without type arguement");
        }

        return new ListItem(annotations, tcp, -1);
    }

    public static ListItem fromItem(ListItem item, int index) {
        return new ListItem(item.annotations, item.tcp, index);
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
