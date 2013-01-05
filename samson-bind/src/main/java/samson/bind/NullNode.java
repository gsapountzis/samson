package samson.bind;

import samson.metadata.Element;

public class NullNode extends TypedNode {

    public static final NullNode INSTANCE = new NullNode();

    private NullNode() {
        super(NodeType.NULL, Element.NULL_ELEMENT);
    }

    @Override
    public Object getObject() {
        return null;
    }

    @Override
    public TypedNode getChild(String name) {
        return null;
    }

}
