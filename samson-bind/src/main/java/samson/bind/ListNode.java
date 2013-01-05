package samson.bind;

import java.util.Collections;
import java.util.List;

import samson.metadata.Element;

import com.google.common.base.Preconditions;

public class ListNode extends StructureNode {

    private final List<?> list;
    private final List<TypedNode> values;

    ListNode(Element element, List<?> list, List<TypedNode> values) {
        super(NodeType.LIST, element);
        this.list = Preconditions.checkNotNull(list);
        this.values = Collections.unmodifiableList(Preconditions.checkNotNull(values));
    }

    @Override
    public Object getObject() {
        return getList();
    }

    public List<?> getList() {
        return list;
    }

    public List<TypedNode> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }

    public TypedNode getValue(int index) {
        return values.get(index);
    }

    @Override
    public TypedNode getValue(String name) {
        int index = getIndex(name);
        if (index >= 0 && index < values.size()) {
            return getValue(index);
        }
        else {
            return null;
        }
    }

    private int getIndex(String name) {
        try {
            return Integer.parseInt(name);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

}
