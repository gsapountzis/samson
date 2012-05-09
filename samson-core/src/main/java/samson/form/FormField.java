package samson.form;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;

import samson.JForm.Field;
import samson.JForm.Messages;
import samson.bind.BinderFactory;
import samson.convert.ConverterException;
import samson.metadata.ElementRef;
import samson.metadata.TypeClassPair;
import samson.utils.Utils;

class FormField implements Field, Messages {

    private final FormFactory factory;

    private final ElementRef ref;
    private final FormNode node;

    FormField(FormFactory factory, ElementRef ref, FormNode node) {
        this.factory = factory;
        this.ref = ref;
        this.node = node;
    }

    // -- Field

    @Override
    public Object getObjectValue() {
        return ref.accessor.get();
    }

    @Override
    public String getValue() {
        return Utils.getFirst(getValues());
    }

    @Override
    public List<String> getValues() {
        BinderFactory binderFactory = factory.getBinderFactory();

        if (node.isConversionError()) {
            return node.getStringValues();
        }
        else {
            return binderFactory.toStringList(ref.element, ref.accessor.get());
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
        if (element == null)
            return;

        for (ConstraintDescriptor<?> constraint : element.getConstraintDescriptors()) {
            Annotation annotation = constraint.getAnnotation();
            String message = annotation.annotationType().getSimpleName();
            messages.add(message);
        }
    }

}
