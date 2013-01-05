package samson.bind;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import samson.metadata.Element;

import com.google.common.base.Preconditions;

public class MapNode extends StructureNode {

    private final Map<?,?> map;
    private final Map<String,TypedNode> values;

    MapNode(Element element, Map<?,?> map, Map<String,TypedNode> values) {
        super(NodeType.MAP, element);
        this.map = Preconditions.checkNotNull(map);
        this.values = Collections.unmodifiableMap(Preconditions.checkNotNull(values));
    }

    @Override
    public Object getObject() {
        return getMap();
    }

    public Map<?,?> getMap() {
        return map;
    }

    public Map<String,TypedNode> getValues() {
        return values;
    }

    public Set<String> getNames() {
        return values.keySet();
    }

    @Override
    public TypedNode getValue(String name) {
        return values.get(name);
    }

}
