package samson.metadata;

import java.lang.annotation.Annotation;
import java.util.Map;

public class MapEntry extends Element {

    public final String key;

    public final TypeClassPair keyTcp;

    private MapEntry(Annotation[] annotations, TypeClassPair tcp, TypeClassPair keyTcp, String key) {
        super(annotations, tcp);
        this.keyTcp = keyTcp;
        this.key = key;
    }

    public static MapEntry fromMap(Element map) {
        Annotation[] annotations = ListItem.component(map.annotations);

        TypeClassPair keyTcp = ReflectionHelper.getTypeArgumentAndClass(map.tcp.t, 0);
        TypeClassPair valTcp = ReflectionHelper.getTypeArgumentAndClass(map.tcp.t, 1);
        if (keyTcp == null || valTcp == null) {
            throw new IllegalArgumentException("Parameterized type without type arguement");
        }

        return new MapEntry(annotations, valTcp, keyTcp, null);
    }

    public static MapEntry fromEntry(MapEntry entry, String key) {
        return new MapEntry(entry.annotations, entry.tcp, entry.keyTcp, key);
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
