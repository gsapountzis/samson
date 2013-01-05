package samson.bind;

import samson.metadata.Element;

public class AnyNode extends TypedNode {

    private final Object value;

    public AnyNode(Element element, Object value) {
        super(NodeType.ANY, element);
        this.value = value;
    }

    @Override
    public Object getObject() {
        return value;
    }

    @Override
    public TypedNode getChild(String name) {
        return null;
    }

}
