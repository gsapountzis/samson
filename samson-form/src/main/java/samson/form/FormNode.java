package samson.form;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;

import samson.bind.BeanNode;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.bind.ListNode;
import samson.bind.MapNode;
import samson.bind.StructureNode;
import samson.bind.TypedNode;
import samson.bind.ValueNode;
import samson.convert.ConverterException;
import samson.metadata.Element;
import samson.metadata.TypeClassPair;
import samson.property.ParseNode;
import samson.property.ParsePath;
import samson.utils.Utils;

import com.google.common.base.Preconditions;

public class FormNode {

    private final FormProvider factory;
    private final String name;
    private final TypedNode node;

    private final Map<String, FormNode> children = new LinkedHashMap<String, FormNode>();

    private final Set<ConstraintViolation<?>> constraintViolations = new LinkedHashSet<ConstraintViolation<?>>();
    private final List<String> infos = new ArrayList<String>();
    private final List<String> errors = new ArrayList<String>();

    public FormNode(FormProvider factory, String name, TypedNode node) {
        this.factory = Preconditions.checkNotNull(factory);
        this.name = Preconditions.checkNotNull(name);
        this.node = Preconditions.checkNotNull(node);
        addChildren();
    }

    private void addChildren() {
        if (node instanceof StructureNode) {
            if (node instanceof ListNode) {
                ListNode listNode = (ListNode) node;
                for (int i = 0; i < listNode.getValues().size(); i++) {
                    String name = Integer.toString(i);
                    TypedNode child = listNode.getValues().get(i);
                    if (child != null) {
                        children.put(name, new FormNode(factory, name, child));
                    }
                }
            }
            else if (node instanceof MapNode) {
                MapNode mapNode = (MapNode) node;
                for (Entry<String,TypedNode> e : mapNode.getValues().entrySet()) {
                    String name = e.getKey();
                    TypedNode child = e.getValue();
                    children.put(name, new FormNode(factory, name, child));
                }
            }
            else if (node instanceof BeanNode) {
                BeanNode beanNode = (BeanNode) node;
                for (Entry<String,TypedNode> e : beanNode.getValues().entrySet()) {
                    String name = e.getKey();
                    TypedNode child = e.getValue();
                    children.put(name, new FormNode(factory, name, child));
                }
            }
        }
    }

    // -- Value

    public Object getObjectValue() {
        return node.getObject();
    }

    public String getValue() {
        return Utils.getFirst(getValues());
    }

    public List<String> getValues() {
        if (isConversionError()) {
            return getStringValues();
        }
        else {
            BinderFactory binderFactory = factory.getBinderFactory();
            return binderFactory.toStringList(node.getElement(), node.getObject());
        }
    }

    public List<String> getStringValues() {
        if (node instanceof ValueNode) {
            ValueNode valueNode = (ValueNode) node;
            return valueNode.getStringValues();
        }
        return null;
    }

    public ConverterException getConversionFailure() {
        if (node instanceof ValueNode) {
            if (node instanceof ValueNode.ErrorValueNode) {
                ValueNode.ErrorValueNode errorNode = (ValueNode.ErrorValueNode) node;
                ConverterException conversionError = errorNode.getCause();
                return conversionError;
            }
        }
        return null;
    }

    public boolean isConversionError() {
        if (node instanceof ValueNode) {
            if (node instanceof ValueNode.ErrorValueNode) {
                return true;
            }
        }
        return false;
    }

    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return constraintViolations;
    }

    public void addConstraintViolation(ConstraintViolation<?> constraintViolation) {
        constraintViolations.add(constraintViolation);
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

    // -- Path

    public FormNode path(int index) {
        return child(Integer.toString(index));
    }

    public FormNode path(String name) {
        return child(name);
    }

    public FormNode indexPath(int index) {
        return child(Integer.toString(index));
    }

    public FormNode propertyPath(String name) {
        return child(name);
    }

    private FormNode child(String name) {
        FormNode child = children.get(name);
        if (child == null) {
            BinderFactory binderFactory = factory.getBinderFactory();
            Binder binder = binderFactory.getBinder(node.getElement(), true);
            TypedNode anyChild = binder.child(name, node.getObject());

            child = new FormNode(factory, name, anyChild);
            children.put(name, child);
        }
        return child;
    }

    static FormNode getNode(FormNode root, String param) throws ParseException {
        ParsePath path = Utils.isNullOrEmpty(param) ? ParsePath.of() : ParsePath.of(param);

        FormNode child = root;
        for (ParseNode node : path) {
            child = child.child(node.getName());
        }
        return child;
    }

    // -- Messages

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

    public String getConversionInfo() {
        return getDefaultConversionInfo();
    }

    public List<String> getValidationInfos() {
        return getDefaultValidationInfos();
    }

    public String getConversionError() {
        if (isConversionError()) {
            ConverterException conversionError = getConversionFailure();
            return conversionError.getMessage();
        }
        else {
            return null;
        }
    }

    public List<String> getValidationErrors() {
        List<String> messages = new ArrayList<String>();
        for (ConstraintViolation<?> violation : constraintViolations) {
            messages.add(violation.getMessage());
        }
        return messages;
    }

    public List<String> getAllErrors() {
        List<String> messages = new ArrayList<String>();
        if (isConversionError()) {
            messages.add(getConversionError());
        }
        messages.addAll(getValidationErrors());
        messages.addAll(errors);
        return messages;
    }

    // -- Default Messages

    private String getDefaultConversionInfo() {
        Element element = node.getElement();
        if (element != Element.NULL_ELEMENT) {
            TypeClassPair tcp = element.tcp;
            String message = tcp.c.getSimpleName();
            return message;
        }
        else {
            return null;
        }
    }

    private List<String> getDefaultValidationInfos() {
        ValidatorFactory validatorFactory = factory.getValidatorFactory();
        if (validatorFactory == null) {
            return Collections.emptyList();
        }

        Validator validator = validatorFactory.getValidator();

        Element element = node.getElement();
        ElementDescriptor decl = ValidatorExt.getElementDescriptorDecl(validator, element);
        ElementDescriptor type = ValidatorExt.getElementDescriptorType(validator, element);

        List<String> messages = new ArrayList<String>();
        getDefaultValidationInfos(messages, decl);
        getDefaultValidationInfos(messages, type);
        return messages;
    }

    private void getDefaultValidationInfos(List<String> messages, ElementDescriptor element) {
        if (element == null) {
            return;
        }

        for (ConstraintDescriptor<?> constraint : element.getConstraintDescriptors()) {
            Annotation annotation = constraint.getAnnotation();
            String message = annotation.annotationType().getSimpleName();
            messages.add(message);
        }
    }

    // -- Tree Computations (visitor / functional)

    public void print(int indent, StringBuilder sb) {
        boolean error = isConversionError();
        sb.append("[").append(error ? "X" : " ").append("] ");

        int size = constraintViolations.size();
        sb.append("[").append((size > 0) ? size : " ").append("] ");

        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append(name).append("\n");
    }

    public void printTree(int indent, StringBuilder sb) {
        print(indent, sb);

        indent += name.length();

        for (FormNode child : children.values()) {
            child.printTree(indent, sb);
        }
    }

}
