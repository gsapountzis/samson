package samson.metadata;

import java.lang.annotation.Annotation;

public class ResolvedListType {

    private static final Annotation[] NO_ANNOTATIONS = new Annotation[0];

    private final TypeClassPair tcp;
    private final TypeClassPair itemTcp;
    private final Element itemElement;

    public ResolvedListType(TypeClassPair tcp) {
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

}
