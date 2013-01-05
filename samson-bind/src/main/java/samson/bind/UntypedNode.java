package samson.bind;

import java.text.ParseException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.property.ParseNode;
import samson.property.ParsePath;
import samson.utils.Utils;

public class UntypedNode {

    private static final Logger LOGGER = LoggerFactory.getLogger(UntypedNode.class);

    private final Map<String, UntypedNode> values = new LinkedHashMap<String, UntypedNode>();
    private List<String> stringValues = null;

    private UntypedNode() {
    }

    // -- Composite

    public boolean hasChildren() {
        return !values.isEmpty();
    }

    public Map<String, UntypedNode> getChildren() {
        return Collections.unmodifiableMap(values);
    }

    public Set<String> getChildNames() {
        return values.keySet();
    }

    public UntypedNode getChild(String name) {
        return values.get(name);
    }

    private UntypedNode getOrAddChild(String name) {
        UntypedNode value = getChild(name);
        if (value == null) {
            value = new UntypedNode();
            values.put(name, value);
        }
        return value;
    }

    // -- Multivalue

    public List<String> getStringValues() {
        return stringValues;
    }

    private void setStringValues(List<String> values) {
        this.stringValues = values;
    }

    // -- Parse

    public static UntypedNode parse(Map<String, List<String>> params) {
        UntypedNode unnamed = new UntypedNode();

        for (Entry<String, List<String>> entry : params.entrySet()) {
            String param = entry.getKey();
            List<String> values = entry.getValue();

            try {
                UntypedNode node = getNode(unnamed, param);
                node.setStringValues(values);
            } catch (ParseException e) {
                LOGGER.warn("Cannot parse parameter name {}", param);
            }
        }

        return unnamed;
    }

    public static UntypedNode getNode(UntypedNode root, String param) throws ParseException {
        ParsePath path = Utils.isNullOrEmpty(param) ? ParsePath.of() : ParsePath.of(param);

        UntypedNode child = root;
        for (ParseNode node : path) {
            child = child.getOrAddChild(node.getName());
        }
        return child;
    }

}
