package samson.metadata;

import java.util.ArrayList;
import java.util.List;

import samson.jersey.core.reflection.ReflectionHelper;

public class ListTcp {

    private final TypeClassPair tcp;
    private final TypeClassPair elementTcp;

    public ListTcp(TypeClassPair tcp) {
        this.tcp = tcp;
        this.elementTcp = ReflectionHelper.getTypeArgumentAndClass(tcp.t);

        if (elementTcp == null) {
            throw new IllegalArgumentException("Parameterized type without type arguement");
        }
    }

    public TypeClassPair getTcp() {
        return tcp;
    }

    public TypeClassPair getElementTcp() {
        return elementTcp;
    }

    public static List<?> createInstance(TypeClassPair listTcp) {
        Class<?> listClass = listTcp.c;

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

    public static Element.Accessor createAccessor(final List<?> list, final int index) {
        if (list == null) {
            return Element.Accessor.NULL_ACCESSOR;
        }

        return new Element.Accessor() {

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
