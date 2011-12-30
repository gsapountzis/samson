package samson.form;

import static samson.Configuration.CONVERSION_ERROR_MESSAGE_TEMPLATE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.bind.Binder;
import samson.bind.BinderNode;
import samson.bind.BinderType;
import samson.convert.ConverterException;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.ElementRef;

public class FormNode implements BinderNode<FormNode> {
    private Binder binder = Binder.NULL_BINDER;

    private final Node node;
    private Map<Node, FormNode> children;

    private List<String> stringValues;
    private ConverterException conversionError = null;
    private Set<ConstraintViolation<?>> constraintViolations = new LinkedHashSet<ConstraintViolation<?>>();
    private List<String> infos = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();

    public FormNode(Node node) {
        this.node = node;
        this.children = new LinkedHashMap<Node, FormNode>();
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

    public void setStringValues(List<String> values) {
        this.stringValues = values;
    }

    public List<String> getStringValues() {
        return stringValues;
    }

    public boolean isConversionError() {
        return (conversionError != null);
    }

    public ConverterException getConversionFailure() {
        return conversionError;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }

    public void addConstraintViolation(ConstraintViolation<?> constraintViolation) {
        constraintViolations.add(constraintViolation);
    }

    public List<String> getInfos() {
        return infos;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void info(String msg) {
        infos.add(msg);
    }

    public void error(String msg) {
        errors.add(msg);
    }

    public String getConversionError() {
        boolean error = isConversionError();
        if (error) {
            String stringValue = Utils.getFirst(stringValues);
            return getConversionErrorMessage(stringValue);
        }
        return null;
    }

    public List<String> getValidationErrors() {
        List<String> messages = new ArrayList<String>();
        for (ConstraintViolation<?> violation : constraintViolations) {
            messages.add(violation.getMessage());
        }
        return messages;
    }

    private static String getConversionErrorMessage(String value) {
        return String.format(CONVERSION_ERROR_MESSAGE_TEMPLATE, value);
    }

    // -- Tree Computations (visitor / functional)

    public void convert(Form<?> form) {
        if (binder.getType() == BinderType.STRING) {
            ElementRef ref = binder.getRef();
            Conversion conversion = form.fromStringList(ref.element, stringValues);
            if (conversion != null) {
                if (conversion.isError()) {
                    conversionError = conversion.getCause();
                }
                else {
                    ref.accessor.set(conversion.getValue());
                }
            }
        }
    }

    public boolean isError() {
        if (isConversionError()) {
            return true;
        }
        if (!constraintViolations.isEmpty()) {
            return true;
        }
        if (!errors.isEmpty()) {
            return true;
        }
        return false;
    }

    public void print(StringBuilder sb, int indent) {
        boolean error = isConversionError();
        sb.append("[").append(error ? "X" : " ").append("] ");

        int size = constraintViolations.size();
        sb.append("[").append((size > 0) ? size : " ").append("] ");

        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        String s = node.toString();
        sb.append(s).append("\n");
    }

    public void convertTree(Form<?> form) {
        convert(form);

        for (FormNode child : children.values()) {
            child.convertTree(form);
        }
    }

    public boolean isTreeError() {
        if (isError()) {
            return true;
        }

        for (FormNode child : children.values()) {
            if (child.isTreeError()) {
                return true;
            }
        }

        return false;
    }

    public void printTree(StringBuilder sb, int indent) {
        print(sb, indent);

        String s = node.toString();
        indent += s.length();

        for (FormNode child : children.values()) {
            child.printTree(sb, indent);
        }
    }

}
