package samson.metadata;

import java.lang.annotation.Annotation;

public class ResolvedMapType {

    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    private final TypeClassPair tcp;
    private final TypeClassPair keyTcp;
    private final TypeClassPair valueTcp;
    private final Element valueElement;

    public ResolvedMapType(TypeClassPair tcp) {
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

}
