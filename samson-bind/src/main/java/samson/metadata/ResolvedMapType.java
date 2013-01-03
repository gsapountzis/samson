package samson.metadata;

public class ResolvedMapType {

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

        this.valueElement = new Element(Element.NO_ANNOTATIONS, valTcp);
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
