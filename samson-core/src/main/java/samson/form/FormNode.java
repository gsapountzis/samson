package samson.form;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.Element;
import samson.bind.Binder;
import samson.bind.BinderNode;
import samson.bind.BinderType;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.ElementRef;

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

    public boolean isConversionError() {
        return (conversionError != null);
    }

    public Throwable getConversionError() {
        return conversionError;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }

    public void addConstraintViolation(ConstraintViolation<?> constraintViolation) {
        constraintViolations.add(constraintViolation);
    }

    // -- Node Computations

    public void convert(AbstractForm<?> form) {
        if (binder != null) {
            if (binder.getType() == BinderType.STRING) {
                ElementRef ref = binder.getElementRef();
                Conversion conversion = form.fromStringList(ref.element, stringValues);
                if (conversion.isError()) {
                    conversionError = conversion.getCause();
                }
                else {
                    ref.accessor.set(conversion.getValue());
                }
            }
        }
    }

    public Element getElement() {
        if (binder != null) {
            ElementRef ref = binder.getElementRef();
            return ref.element;
        }
        else {
            return null;
        }
    }

    public Object getObjectValue() {
        if (binder != null) {
            ElementRef ref = binder.getElementRef();
            return ref.accessor.get();
        }
        else {
            return null;
        }
    }

    public String getValue(AbstractForm<?> form) {
        if (isConversionError()) {
            return Utils.getFirst(stringValues);
        }
        else {
            if (binder != null) {
                ElementRef ref = binder.getElementRef();
                return form.toStringValue(ref.element, ref.accessor.get());
            }
            else {
                return null;
            }
        }
    }

    public List<String> getValues(AbstractForm<?> form) {
        if (isConversionError()) {
            return stringValues;
        }
        else {
            if (binder != null) {
                ElementRef ref = binder.getElementRef();
                return form.toStringList(ref.element, ref.accessor.get());
            }
            else {
                return Collections.emptyList();
            }
        }
    }

    public boolean isError() {
        if (isConversionError()) {
            return true;
        }
        if (constraintViolations.size() > 0) {
            return true;
        }
        return false;
    }

    // -- Tree Computations (visitor / functional)

    public void convertTree(AbstractForm<?> form) {
        convert(form);

        for (FormNode child : children.values()) {
            child.convertTree(form);
        }
    }

    public void conversionErrors(Set<Throwable> conversionErrors) {
        if (isConversionError()) {
            conversionErrors.add(conversionError);
        }

        for (FormNode child : children.values()) {
            child.conversionErrors(conversionErrors);
        }
    }

    public boolean hasErrors() {
        if (isError()) {
            return true;
        }

        for (FormNode child : children.values()) {
            if (child.hasErrors()) {
                return true;
            }
        }

        return false;
    }

    public void printTree(StringBuilder sb, int indent) {

        boolean error = isConversionError();
        sb.append("[").append(error ? "X" : " ").append("] ");

        int size = constraintViolations.size();
        sb.append("[").append((size > 0) ? size : " ").append("] ");

        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        String s = node.toString();
        sb.append(s).append("\n");

        for (FormNode child : children.values()) {
            child.printTree(sb, indent + s.length());
        }
    }

}
