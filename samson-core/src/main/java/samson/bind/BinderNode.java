package samson.bind;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BinderNode implements ParamNode<BinderNode> {

    private final Binder binder;

    private final String name;

    private List<String> stringValues;

    private Map<String, BinderNode> children;

    public BinderNode(Binder binder, String name) {
        this.binder = binder;
        this.name = name;
        this.children = new LinkedHashMap<String, BinderNode>();
        this.stringValues = null;
    }

    public Binder getBinder() {
        return binder;
    }

    // -- Tree structure

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getStringValues() {
        return stringValues;
    }

    public void setStringValues(List<String> stringValues) {
        this.stringValues = stringValues;
    }

    public void addChild(BinderNode child) {
        String name = child.getName();
        children.put(name, child);
    }

    @Override
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }

    @Override
    public BinderNode getChild(String name) {
        return children.get(name);
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public Collection<BinderNode> getChildren() {
        return children.values();
    }

}
