package samson.metadata;

import java.lang.annotation.Annotation;
import java.util.List;

public class ListMetadata {

    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    private final TypeClassPair tcp;
    private final TypeClassPair itemTcp;
    private final Element itemElement;

    public ListMetadata(TypeClassPair tcp) {
        this.tcp = tcp;

        TypeClassPair itemTcp = ReflectionHelper.getTypeArgumentAndClass(tcp.t);
        if (itemTcp == null) {
            throw new IllegalArgumentException("Parameterized type without type arguement");
        }
        this.itemTcp = itemTcp;

        this.itemElement = new Element(NO_ANNOTATIONS, itemTcp);
    }

    public TypeClassPair getTcp() {
        return tcp;
    }

    public TypeClassPair getItemTcp() {
        return itemTcp;
    }

    public Element getItem() {
        return itemElement;
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
