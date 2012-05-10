package samson.form;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;

import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.bind.BinderNode;
import samson.convert.ConverterException;
import samson.metadata.ElementRef;
import samson.metadata.TypeClassPair;
import samson.utils.Utils;

public class FormNode implements BinderNode<FormNode> {

    private static final String CONVERSION_ERROR_MESSAGE_TEMPLATE = "invalid value '%s'";

    private final FormProvider factory;

    private final String name;
    private final Map<String, FormNode> children = new LinkedHashMap<String, FormNode>();

    private ElementRef ref = ElementRef.NULL_REF;
    private List<String> stringValues = null;
    private ConverterException conversionError = null;
    private Set<ConstraintViolation<?>> constraintViolations = new LinkedHashSet<ConstraintViolation<?>>();
    private List<String> infos = new ArrayList<String>();
    private List<String> errors = new ArrayList<String>();

    public FormNode(FormProvider factory, String name) {
        this.factory = factory;
        this.name = name;
    }

    // -- Node

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasChild(String name) {
        return children.containsKey(name);
    }

    @Override
    public FormNode getChild(String name) {
        return children.get(name);
    }

    @Override
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    @Override
    public Collection<FormNode> getChildren() {
        return children.values();
    }

    // -- Reference

    @Override
    public ElementRef getRef() {
        return ref;
    }

    @Override
    public void setRef(ElementRef ref) {
        this.ref = ref;
    }

    // -- Value

    @Override
    public List<String> getStringValues() {
        return stringValues;
    }

    @Override
    public void setStringValues(List<String> values) {
        this.stringValues = values;
    }

    @Override
    public ConverterException getConversionFailure() {
        return conversionError;
    }

    @Override
    public void setConversionFailure(ConverterException conversionError) {
        this.conversionError = conversionError;
    }

    public boolean isConversionError() {
        return (conversionError != null);
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
        return path(Integer.toString(index));
    }

    public FormNode path(String name) {
        return path(name, true);
    }

    FormNode path(String name, boolean setRef) {
        FormNode child = children.get(name);
        if (child == null) {
            child = new FormNode(factory, name);
            children.put(name, child);

            ElementRef childRef = child.getRef();
            if (setRef && (childRef == ElementRef.NULL_REF)) {
                BinderFactory binderFactory = factory.getBinderFactory();
                Binder binder = binderFactory.getBinder(ref, true, false);
                childRef = binder.getChildRef(name);
                child.setRef(childRef);
            }
        }
        return child;
    }

    // -- Value

    public Object getObjectValue() {
        return ref.accessor.get();
    }

    public String getValue() {
        return Utils.getFirst(getValues());
    }

    public List<String> getValues() {
        BinderFactory binderFactory = factory.getBinderFactory();

        if (isConversionError()) {
            return getStringValues();
        }
        else {
            return binderFactory.toStringList(ref.element, ref.accessor.get());
        }
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

    public List<String> getAllErrors() {
        List<String> messages = new ArrayList<String>();
        if (isConversionError()) {
            messages.add(getConversionError());
        }
        messages.addAll(getValidationErrors());
        messages.addAll(errors);
        return messages;
    }

    private static String getConversionErrorMessage(String value) {
        return String.format(CONVERSION_ERROR_MESSAGE_TEMPLATE, value);
    }

    // -- Default Messages

    private String getDefaultConversionInfo() {
        if (ref != ElementRef.NULL_REF) {
            TypeClassPair tcp = ref.element.tcp;
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

        ElementDescriptor decl = ValidatorExt.getElementDescriptorDecl(validator, ref.element);
        ElementDescriptor type = ValidatorExt.getElementDescriptorType(validator, ref.element);

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
        String name = (this.name == null) ? "" : this.name;

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
        String name = (this.name == null) ? "" : this.name;

        print(indent, sb);

        indent += name.length();

        for (FormNode child : children.values()) {
            child.printTree(indent, sb);
        }
    }

}
