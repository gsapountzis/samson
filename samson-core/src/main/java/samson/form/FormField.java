package samson.form;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import samson.Element;
import samson.JForm.Field;
import samson.JForm.Messages;
import samson.TypeClassPair;
import samson.convert.ConverterException;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.ElementRef;

class FormField implements Field, Messages {

    private final Form<?> form;
    private final Path path;
    private final FormNode node;
    private final ElementRef ref;

    FormField(Form<?> form, Path path, FormNode node) {
        this.form = form;
        this.path = path;
        this.node = node;
        this.ref = form.getPathElementRef(path);
    }

    // -- Field

    @Override
    public Element getElement() {
        if (ref != ElementRef.NULL_REF) {
            return ref.element;
        }
        else {
            return null;
        }
    }

    @Override
    public Object getObjectValue() {
        if (ref != ElementRef.NULL_REF) {
            return ref.accessor.get();
        }
        else {
            return null;
        }
    }

    @Override
    public String getValue() {
        if (node.isConversionError()) {
            return Utils.getFirst(node.getStringValues());
        }
        else {
            if (ref != ElementRef.NULL_REF) {
                return form.toStringValue(ref.element, ref.accessor.get());
            }
            else {
                return null;
            }
        }
    }

    @Override
    public List<String> getValues() {
        if (node.isConversionError()) {
            return node.getStringValues();
        }
        else {
            if (ref != ElementRef.NULL_REF) {
                return form.toStringList(ref.element, ref.accessor.get());
            }
            else {
                return Collections.emptyList();
            }
        }
    }

    @Override
    public boolean isError() {
        return node.isError();
    }

    @Override
    public ConverterException getConversionFailure() {
        return node.getConversionError();
    }

    @Override
    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return node.getConstraintViolations();
    }

    // -- Messages

    @Override
    public Messages getMessages() {
        return this;
    }

    @Override
    public String getConversionInfo() {
        return getDefaultConversionInfo();
    }

    @Override
    public String getConversionError() {
        boolean error = node.isConversionError();
        if (error) {
            String stringValue = Utils.getFirst(node.getStringValues());
            return getConversionErrorMessage(stringValue);
        }
        return null;
    }

    @Override
    public List<String> getValidationInfos() {
        return getDefaultValidationInfos();
    }

    @Override
    public List<String> getValidationErrors() {
        Set<ConstraintViolation<?>> constraintViolations = node.getConstraintViolations();
        List<String> messages = new ArrayList<String>();
        for (ConstraintViolation<?> violation : constraintViolations) {
            messages.add(violation.getMessage());
        }
        return messages;
    }

    @Override
    public List<String> getInfos() {
        return node.getInfos();
    }

    @Override
    public List<String> getErrors() {
        return node.getErrors();
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
        List<String> messages = new ArrayList<String>();
        ElementDescriptorTuple element = getPathValidationElement();
        if (element != null) {
            getDefaultValidationInfos(messages, element.type);
            getDefaultValidationInfos(messages, element.decl);
        }
        return messages;
    }

    private void getDefaultValidationInfos(List<String> messages, ElementDescriptor element) {
        if (element != null) {
            for (ConstraintDescriptor<?> constraint : element.getConstraintDescriptors()) {
                Annotation annotation = constraint.getAnnotation();
                String message = annotation.annotationType().getSimpleName();
                messages.add(message);
            }
        }
    }

    private static class ElementDescriptorTuple {
        final ElementDescriptor type;
        final ElementDescriptor decl;

        ElementDescriptorTuple(ElementDescriptor type, ElementDescriptor decl) {
            this.type = type;
            this.decl = decl;
        }
    }

    private ElementDescriptorTuple getPathValidationElement() {
        if (ref != ElementRef.NULL_REF) {
            Validator validator = form.getValidator();

            TypeClassPair tcp = ref.element.tcp;
            BeanDescriptor bean = validator.getConstraintsForClass(tcp.c);

            if (path.isEmpty()) {
                return new ElementDescriptorTuple(bean, /* method parameter */ null);
            }
            else {
                Path parent = path.head();
                Node child = path.tail();
                ElementRef parentRef = form.getPathElementRef(parent);

                TypeClassPair parentTcp = parentRef.element.tcp;
                BeanDescriptor parentBean = validator.getConstraintsForClass(parentTcp.c);
                PropertyDescriptor property = parentBean.getConstraintsForProperty(child.getName());

                return new ElementDescriptorTuple(bean, property);
            }
        }
        else {
            return null;
        }
    }

    private static final String CONVERSION_ERROR_MESSAGE_TEMPLATE = "invalid value '%s'";

    private static String getConversionErrorMessage(String value) {
        return String.format(CONVERSION_ERROR_MESSAGE_TEMPLATE, value);
    }

}
