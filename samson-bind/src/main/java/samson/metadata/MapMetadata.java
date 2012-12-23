package samson.metadata;

import java.lang.annotation.Annotation;
import java.util.Map;

public class MapMetadata {

    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    private final TypeClassPair tcp;
    private final TypeClassPair keyTcp;
    private final TypeClassPair valueTcp;
    private final Element valueElement;

    public MapMetadata(TypeClassPair tcp) {
        this.tcp = tcp;

        TypeClassPair keyTcp = ReflectionHelper.getTypeArgumentAndClass(tcp.t, 0);
        TypeClassPair valTcp = ReflectionHelper.getTypeArgumentAndClass(tcp.t, 1);
        if (keyTcp == null || valTcp == null) {
            throw new IllegalArgumentException("Parameterized type without type arguement");
        }

        this.keyTcp = keyTcp;
        this.valueTcp = valTcp;

        this.valueElement = new Element(NO_ANNOTATIONS, valTcp);
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

    public Element getValue() {
        return valueElement;
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
