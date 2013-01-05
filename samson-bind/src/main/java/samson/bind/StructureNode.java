package samson.bind;

import samson.metadata.Element;

public abstract class StructureNode extends TypedNode {

    public StructureNode(NodeType type, Element element) {
        super(type, element);
    }

    public abstract TypedNode getValue(String name);

    @Override
    public TypedNode getChild(String name) {
        return getValue(name);
    }

}
