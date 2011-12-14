package samson.metadata;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import samson.jersey.core.reflection.ReflectionHelper;

public class MapTcp {

    private final TypeClassPair tcp;
    private final TypeClassPair keyTcp;
    private final TypeClassPair valueTcp;

    public MapTcp(TypeClassPair tcp) {
        this.tcp = tcp;
        this.keyTcp = ReflectionHelper.getTypeArgumentAndClass(tcp.t, 0);
        this.valueTcp = ReflectionHelper.getTypeArgumentAndClass(tcp.t, 1);

        if (keyTcp == null || valueTcp == null) {
            throw new IllegalArgumentException("Parameterized type without type arguement");
        }
    }

    public TypeClassPair getTcp() {
        return tcp;
    }

    public TypeClassPair getKeyTcp() {
        return keyTcp;
    }

    public TypeClassPair getValueTcp() {
        return valueTcp;
    }

    public Element getValueElement(Annotation[] annotations, String name) {
        return new Element(annotations, valueTcp, name);
    }

    public static Map<?, ?> createInstance(TypeClassPair mapTcp) {
        Class<?> mapClass = mapTcp.c;

        if (!Map.class.isAssignableFrom(mapClass)) {
            throw new IllegalArgumentException();
        }

        if (mapClass.isInterface()) {
            if (mapClass == Map.class) {
                mapClass = HashMap.class;
            }
            else {
                throw new RuntimeException("Unknown map interface");
            }
        }

        try {
            return (Map<?, ?>) mapClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Element.Accessor createAccessor(final Map<?, ?> map, final Object key) {
        if (map == null) {
            return Element.Accessor.NULL_ACCESSOR;
        }

        return new Element.Accessor() {

            @SuppressWarnings("unchecked")
            @Override
            public void set(Object value) {
                ((Map<Object, Object>) map).put(key, value);
            }

            @Override
            public Object get() {
                return map.get(key);
            }
        };
    }

}
