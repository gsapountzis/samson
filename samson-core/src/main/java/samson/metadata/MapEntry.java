package samson.metadata;

import java.lang.annotation.Annotation;
import java.util.Map;

import samson.jersey.core.reflection.ReflectionHelper;

public class MapEntry extends Element {

    public final TypeClassPair keyTcp;

    private MapEntry(Annotation[] annotations, TypeClassPair tcp, TypeClassPair keyTcp) {
        super(annotations, tcp, null);
        this.keyTcp = keyTcp;
    }

    public static MapEntry fromMap(Element map) {
        Annotation[] annotations = ListItem.component(map.annotations);

        TypeClassPair keyTcp = ReflectionHelper.getTypeArgumentAndClass(map.tcp.t, 0);
        TypeClassPair valTcp = ReflectionHelper.getTypeArgumentAndClass(map.tcp.t, 1);
        if (keyTcp == null || valTcp == null) {
            throw new IllegalArgumentException("Parameterized type without type arguement");
        }

        return new MapEntry(annotations, valTcp, keyTcp);
    }

    public Element createElement(String key) {
        return new Element(annotations, tcp, key);
    }

    public static ElementAccessor createAccessor(final Map<?, ?> map, final Object key) {
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
