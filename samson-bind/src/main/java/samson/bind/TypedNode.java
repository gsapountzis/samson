package samson.bind;

import samson.metadata.Element;

import com.google.common.base.Preconditions;

public abstract class TypedNode {

    final NodeType type;
    final Element element;

    public TypedNode(NodeType type, Element element) {
        this.type = Preconditions.checkNotNull(type);
        this.element = Preconditions.checkNotNull(element);
    }

    public NodeType getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public abstract Object getObject();

    public abstract TypedNode getChild(String name);

}
