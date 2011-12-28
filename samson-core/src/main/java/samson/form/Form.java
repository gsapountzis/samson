package samson.form;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import samson.Element;
import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.convert.ConverterException;
import samson.convert.ConverterProvider;
import samson.convert.MultivaluedConverter;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;

class Form<T> implements JForm<T> {

    protected final Form<T> form = this;
    protected final FormNode root;

    protected final Element parameter;
    protected final ElementAccessor parameterAccessor;
    protected final ElementRef parameterRef;

    protected T parameterValue;
    protected boolean hasErrors;

    protected ConverterProvider converterProvider;
    protected BinderFactory binderFactory;
    protected ValidatorFactory validatorFactory;

    Form(FormNode root, Element parameter, T parameterValue) {
        this.root = root;

        this.parameter = parameter;
        this.parameterValue = parameterValue;
        this.parameterAccessor = new ElementAccessor() {

            @Override
            public Object get() {
                return form.parameterValue;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void set(Object value) {
                form.parameterValue = (T) value;
            }

        };
        this.parameterRef = new ElementRef(parameter, parameterAccessor);
    }

    void setConverterProvider(ConverterProvider converterProvider) {
        this.converterProvider = converterProvider;
    }

    void setBinderFactory(BinderFactory binderFactory) {
        this.binderFactory = binderFactory;
    }

    void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    // -- Path

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public JForm<?> path(String path) {
        return new PathForm(this, path);
    }

    @Override
    public JForm<?> dot(String property) {
        return new PathForm(this, property);
    }

    @Override
    public JForm<?> index(String index) {
        return new PathForm(this, "[" + index + "]");
    }

    @Override
    public JForm<?> index(int index) {
        return index(Integer.toString(index));
    }

    // -- Form

    @Override
    public T get() {
        return parameterValue;
    }

    @Override
    public boolean hasErrors() {
        return hasErrors;
    }

    @Override
    public Map<String, ConverterException> getConversionFailures() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Set<ConstraintViolation<T>>> getConstraintViolations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, List<String>> getInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, List<String>> getErrors() {
        throw new UnsupportedOperationException();
    }

    // -- Field

    @Override
    public Field getField() {
        return getField(null);
    }

    @Override
    public Messages getMessages() {
        return getField(null);
    }

    @Override
    public void info(String msg) {
        info(null, msg);
    }

    @Override
    public void error(String msg) {
        error(null, msg);
    }

    // -- Field Path

    FormField getField(String param) {
        Path path = Path.createPath(param);
        FormNode node = root.getDefinedChild(path);
        return new FormField(form, path, node);
    }

    void info(String param, String msg) {
        Path path = Path.createPath(param);
        FormNode node = root.getDefinedChild(path);
        node.info(msg);
    }

    void error(String param, String msg) {
        hasErrors = true;

        Path path = Path.createPath(param);
        FormNode node = root.getDefinedChild(path);
        node.error(msg);
    }

    // -- Callbacks

    ElementRef getPathElementRef(Path path) {
        ElementRef ref = parameterRef;
        for (Node node : path) {
            Binder binder = binderFactory.getBinder(ref, true, false);
            ref = binder.getChildRef(node.getName());
        }
        return ref;
    }

    Validator getValidator() {
        return validatorFactory.getValidator();
    }

    String toStringValue(Element element, Object value) {
        return Utils.getFirst(toStringList(element, value));
    }

    List<String> toStringList(Element element, Object value) {

        @SuppressWarnings("unchecked")
        MultivaluedConverter<Object> extractor = (MultivaluedConverter<Object>) converterProvider.getMultivalued(
                element.tcp.t,
                element.tcp.c,
                element.annotations);

        if (extractor != null) {
            return extractor.toStringList(value);
        }
        else {
            return Collections.emptyList();
        }
    }

    Conversion fromStringList(Element element, List<String> values) {

        MultivaluedConverter<?> extractor = converterProvider.getMultivalued(
                element.tcp.t,
                element.tcp.c,
                element.annotations,
                element.encoded,
                element.defaultValue);

        if (extractor != null) {
            try {
                Object value = extractor.fromStringList(values);
                return Conversion.fromValue(value);
            }
            catch (ConverterException ex) {
                return Conversion.fromError(ex);
            }
        }
        else {
            return null;
        }
    }

}