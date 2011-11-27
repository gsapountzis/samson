package samson.form;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.bind.ParamNode;
import samson.convert.Conversion;
import samson.form.Property.Node;
import samson.form.Property.Path;


public class FormNode implements ParamNode<FormNode> {

    private final Node node;

    private List<String> stringValues;

    private Map<Node, FormNode> children;

    private Conversion conversion;

    private Set<ConstraintViolation<?>> violations;

    public FormNode(Node node) {
        this.node = node;
        this.children = new LinkedHashMap<Node, FormNode>();

        this.conversion = null;
        this.violations = new LinkedHashSet<ConstraintViolation<?>>();
    }

    public Node getNode() {
        return node;
    }

    // -- Tree structure

    @Override
    public String getName() {
        return node.getName();
    }

    @Override
    public List<String> getStringValues() {
        return stringValues;
    }

    public void setStringValues(List<String> values) {
        this.stringValues = values;
    }

    @Override
    public boolean hasChild(String name) {
        Node node = Node.createPrefix(name);
        return hasChild(node);
    }

    @Override
    public FormNode getChild(String name) {
        Node node = Node.createPrefix(name);
        return getChild(node);
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public Collection<FormNode> getChildren() {
        return children.values();
    }

    public boolean hasChild(Node node) {
        return children.containsKey(node);
    }

    public FormNode getChild(Node node) {
        return children.get(node);
    }

    public FormNode getDefinedChild(Node node) {
        FormNode child = children.get(node);
        if (child == null) {
            child = new FormNode(node);
            children.put(node, child);
        }
        return child;
    }

    public FormNode getDefinedChild(Path path) {
        FormNode tree = this;
        for (Node node : path) {
            tree = tree.getDefinedChild(node);
        }
        return tree;
    }

    // -- Node decorations

    public Conversion getConversion() {
        return conversion;
    }

    public void setConversion(Conversion conversion) {
        this.conversion = conversion;
    }

    public Set<ConstraintViolation<?>> getViolations() {
        return violations;
    }

    // -- Visitor or Functional

    public void print(StringBuilder sb, int indent) {

        boolean error = (conversion != null) && conversion.isError();
        sb.append("[").append(error ? "X" : " ").append("] ");

        int size = violations.size();
        sb.append("[").append((size > 0) ? size : " ").append("] ");

        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        String s = node.toString();
        sb.append(s).append("\n");

        for (FormNode child : children.values()) {
            child.print(sb, indent + s.length());
        }
    }

    public boolean isError() {
        if (conversion.isError()) {
            return true;
        }
        if (violations.size() > 0) {
            return true;
        }
        return false;
    }

    public boolean hasErrors() {
        if (this.isError()) {
            return true;
        }

        for (FormNode child : children.values()) {
            if (child.hasErrors()) {
                return true;
            }
        }

        return false;
    }

}
