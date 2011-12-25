package samson.form;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.bind.Binder;
import samson.bind.BinderNode;
import samson.form.Property.Node;
import samson.form.Property.Path;

public class FormNode implements BinderNode<FormNode> {

    private final Node node;

    private Binder binder;

    private Map<Node, FormNode> children;

    private List<String> stringValues;

    private Throwable conversionError;

    private Set<ConstraintViolation<?>> constraintViolations;

    public FormNode(Node node) {
        this.node = node;
        this.children = new LinkedHashMap<Node, FormNode>();

        this.conversionError = null;
        this.constraintViolations = new LinkedHashSet<ConstraintViolation<?>>();
    }

    // -- Binder

    @Override
    public Binder getBinder() {
        return binder;
    }

    @Override
    public void setBinder(Binder binder) {
        this.binder = binder;
    }

    // -- (String) Node

    @Override
    public String getName() {
        return node.getName();
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

    // -- Node

    public Node getNode() {
        return node;
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

    // -- Decorations

    public List<String> getStringValues() {
        return stringValues;
    }

    public void setStringValues(List<String> values) {
        this.stringValues = values;
    }

    public Throwable getConversionError() {
        return conversionError;
    }

    public void setConversionError(Throwable conversionError) {
        this.conversionError = conversionError;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }

    public void addConstraintViolation(ConstraintViolation<?> constraintViolation) {
        constraintViolations.add(constraintViolation);
    }

    public boolean isError() {
        if (conversionError != null) {
            return true;
        }
        if (constraintViolations.size() > 0) {
            return true;
        }
        return false;
    }

    // -- Visitor or Functional

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

    public void print(StringBuilder sb, int indent) {

        boolean error = (conversionError != null);
        sb.append("[").append(error ? "X" : " ").append("] ");

        int size = constraintViolations.size();
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

}
