package samson.bind;

import samson.metadata.Element;

public abstract class Binder {

    final BinderFactory factory;
    final Element element;

    Binder(BinderFactory factory, Element element) {
        this.factory = factory;
        this.element = element;
    }

    public Element getElement() {
        return element;
    }

    /**
     * Returns either an {@link AnyNode} or {@link NullNode}.
     */
    public abstract TypedNode child(String name, Object object);

    /**
     * Returns either a {@link StructureNode} or a {@link ValueNode}.
     */
    public abstract TypedNode parse(UntypedNode untypedNode, Object object);

//  public abstract UntypedNode format(TypedNode node);

    public static final Binder NULL_BINDER = new Binder(null, Element.NULL_ELEMENT) {

        @Override
        public TypedNode child(String name, Object object) {
            return NullNode.INSTANCE;
        }

        @Override
        public TypedNode parse(UntypedNode node, Object object) {
            return NullNode.INSTANCE;
        }

    };

}
