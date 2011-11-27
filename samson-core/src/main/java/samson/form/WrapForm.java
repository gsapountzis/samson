package samson.form;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import javax.ws.rs.core.MultivaluedMap;

import samson.JForm;
import samson.bind.Binder;
import samson.convert.Conversion;
import samson.metadata.Element;
import samson.metadata.ElementRef;
import samson.metadata.TypeClassPair;


/**
 * Wrapping form.
 */
class WrapForm<T> extends AbstractForm<T> {

    public WrapForm(Element parameter, T value) {
        super(parameter);
        this.value = value;
    }

    @Override
    protected MultivaluedMap<String, String> getFormParams() {
        return null;
    }

    @Override
    protected MultivaluedMap<String, String> getQueryParams() {
        return null;
    }

    @Override
    public JForm<T> apply(MultivaluedMap<String, String> params) {
        throw new UnsupportedOperationException("Cannot bind a wrapping form");
    }

    // -- Form methods

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public List<Conversion> getConversionErrors() {
        return Collections.emptyList();
    }

    @Override
    public Set<ConstraintViolation<T>> getViolations() {
        return Collections.emptySet();
    }

    // -- Field methods

    @Override
    public Object getObjectValue(String param) {
        Conversion binding = getConversion(param);
        if (binding == null) {
            return null;
        }
        return binding.getValue();
    }

    @Override
    public String getValue(String param) {
        Conversion binding = getConversion(param);
        if (binding == null) {
            return null;
        }

        Class<?> bindingClass = binding.getTargetClass();
        return toStringValue(bindingClass, binding.getValue());
    }

    @Override
    public List<String> getValues(String param) {
        Conversion binding = getConversion(param);
        if (binding == null) {
            return null;
        }

        Class<?> bindingClass = binding.getTargetClass();
        return toStringList(bindingClass, binding.getValue());
    }

    @Override
    public boolean isError(String param) {
        return false;
    }

    private ElementRef getPathElementRef(String param) {
        ElementRef ref = new ElementRef(parameter, valueAccessor);
        Binder binder = null;

        Property.Path path = Property.Path.createPath(param);
        Iterator<Property.Node> iter = path.iterator();
        while (iter.hasNext()) {
            binder = binderFactory.getBinder(ref, true);

            Property.Node node = iter.next();
            ref = binder.getElementRef(node.getName());
            if (ref == ElementRef.NULL_REF) {
                return ElementRef.NULL_REF;
            }
        }

        return ref;
    }

    @Override
    public Conversion getConversion(String param) {
        ElementRef ref = getPathElementRef(param);
        if (ref != ElementRef.NULL_REF) {

            TypeClassPair paramTcp = ref.element.tcp;
            Object paramValue = ref.accessor.get();

            return Conversion.fromValue(paramTcp.c, paramValue);
        }
        else {
            return null;
        }
    }

    @Override
    public String getConversionMessage(String param) {
        // XXX default message for demo
        return getDefaultConversionMessage(param);
    }

    public String getDefaultConversionMessage(String param) {
        Conversion binding = getConversion(param);
        if (binding == null) {
            return null;
        }

        Class<?> bindingClass = binding.getTargetClass();
        return bindingClass.getSimpleName();
    }

    @Override
    public Set<ConstraintViolation<?>> getViolations(String param) {
        return Collections.emptySet();
    }

    @Override
    public List<String> getValidationMessages(String param) {
        return Collections.emptyList();
    }

    public List<String> getDefaultValidationMessages(String param) {
        // XXX must translate parameter name to javax.validation format: e.g user[username] vs. user.username
        ElementDescriptor element = getElement(param);
        if (element != null) {
            List<String> messages = new ArrayList<String>();
            for (ConstraintDescriptor<?> constraint : element.getConstraintDescriptors()) {
                Annotation annotation = constraint.getAnnotation();
                String name = annotation.annotationType().getSimpleName();
                messages.add(name);
            }
            return messages;
        }
        return Collections.emptyList();
    }

    private ElementDescriptor getElement(String param) {
        Validator validator = validatorFactory.getValidator();

        // XXX must check for root bean
        Class<?> clazz = parameter.tcp.c;
        BeanDescriptor bean = validator.getConstraintsForClass(clazz);
        PropertyDescriptor property = bean.getConstraintsForProperty(param);
        return property;
    }

}
