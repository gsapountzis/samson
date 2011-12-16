package samson.form;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.MultivaluedMap;

import samson.JForm;
import samson.bind.Binder;
import samson.convert.Conversion;
import samson.metadata.Element;
import samson.metadata.ElementRef;


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

        return toStringValue(binding.getElement(), binding.getValue());
    }

    @Override
    public List<String> getValues(String param) {
        Conversion binding = getConversion(param);
        if (binding == null) {
            return null;
        }

        return toStringList(binding.getElement(), binding.getValue());
    }

    @Override
    public boolean isError(String param) {
        return false;
    }

    private ElementRef getPathElementRef(String param) {
        ElementRef ref = new ElementRef(parameter, valueAccessor);

        Property.Path path = Property.Path.createPath(param);
        for (Property.Node node : path) {
            Binder binder = binderFactory.getBinder(ref, true);
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

            Element paramElement = ref.element;
            Object paramValue = ref.accessor.get();

            return Conversion.fromValue(paramElement, paramValue);
        }
        else {
            return null;
        }
    }

    @Override
    public Set<ConstraintViolation<?>> getViolations(String param) {
        return Collections.emptySet();
    }

    @Override
    String getConversionError(String param) {
        return null;
    }

    @Override
    List<String> getValidationErrors(String param) {
        return Collections.emptyList();
    }

}
