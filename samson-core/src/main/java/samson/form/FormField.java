package samson.form;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;

import samson.Element;
import samson.JForm.Field;
import samson.JForm.Messages;
import samson.TypeClassPair;
import samson.convert.ConverterException;
import samson.metadata.ElementRef;

class FormField implements Field, Messages {

    private final Form<?> form;
    private final FormNode node;
    private final ElementRef ref;
    private final Element parentElement;

    FormField(Form<?> form, FormNode node, ElementRef ref, Element parentElement) {
        this.form = form;
        this.node = node;
        this.ref = ref;
        this.parentElement = parentElement;
    }

    // -- Field

    @Override
    public Element getElement() {
        return ref.element;
    }

    @Override
    public Object getObjectValue() {
        return ref.accessor.get();
    }

    @Override
    public String getValue() {
        if (node.isConversionError()) {
            return Utils.getFirst(node.getStringValues());
        }
        else {
            return form.toStringValue(ref.element, ref.accessor.get());
        }
    }

    @Override
    public List<String> getValues() {
        if (node.isConversionError()) {
            return node.getStringValues();
        }
        else {
            return form.toStringList(ref.element, ref.accessor.get());
        }
    }

    @Override
    public boolean isError() {
        return node.isError();
    }

    @Override
    public ConverterException getConversionFailure() {
        return node.getConversionFailure();
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
        return node.getConversionError();
    }

    @Override
    public List<String> getValidationInfos() {
        return getDefaultValidationInfos();
    }

    @Override
    public List<String> getValidationErrors() {
        return node.getValidationErrors();
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
        Validator validator = form.getValidator();

        ElementDescriptor decl = null;
        if (parentElement != Element.NULL_ELEMENT) {
            // should check for method parameter vs. bean property below
            if (parentElement == Element.NULL_ELEMENT) {
                // method parameter
                decl = null;
            }
            else {
                // bean property
                TypeClassPair parentTcp = parentElement.tcp;
                BeanDescriptor parentBean = validator.getConstraintsForClass(parentTcp.c);
                // should use property name below, not element.name which comes from JAX-RS annotations
                decl = parentBean.getConstraintsForProperty(ref.element.name);
            }
        }

        BeanDescriptor type = null;
        if (ref != ElementRef.NULL_REF) {
            TypeClassPair tcp = ref.element.tcp;
            type = validator.getConstraintsForClass(tcp.c);
        }

        List<String> messages = new ArrayList<String>();
        getDefaultValidationInfos(messages, decl);
        getDefaultValidationInfos(messages, type);
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

}
