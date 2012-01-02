package samson.metadata;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import samson.jersey.core.reflection.ReflectionHelper;

public class MapTcp {

    private final Annotation[] valueAnnotations;

    private final TypeClassPair tcp;
    private final TypeClassPair keyTcp;
    private final TypeClassPair valueTcp;

    public MapTcp(Element element) {
        this.valueAnnotations = ListTcp.component(element.annotations);

        this.tcp = element.tcp;
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

    public Map<?, ?> createInstance() {
        Class<?> mapClass = tcp.c;

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

    public Element createValueElement(String key) {
        return new Element(valueAnnotations, valueTcp, key);
    }

    public static ElementAccessor createValueAccessor(final Map<?, ?> map, final Object key) {
        if (map == null) {
            return ElementAccessor.NULL_ACCESSOR;
        }

        return new ElementAccessor() {

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
