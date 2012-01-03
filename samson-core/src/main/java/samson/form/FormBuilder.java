package samson.form;

import static samson.Configuration.DISABLE_VALIDATION;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.JForm;
import samson.JFormBuilder;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;

class FormBuilder implements JFormBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormBuilder.class);

    private final FormFactory factory;
    private final String path;
    private final Map<String, List<String>> params;

    private Element element;

    FormBuilder(FormFactory factory, String path) {
        this(factory, path, null);
    }

    FormBuilder(FormFactory factory, String path, Map<String, List<String>> params) {
        this.factory = factory;
        this.path = path;
        this.params = params;
    }

    // -- Element

    private <T> Element element(Class<T> type) {
        Annotation[] annotations = new Annotation[0];
        return new Element(annotations, type, type, null);
    }

    @Override
    public <T> JForm<T> wrap(Class<T> type) {
        this.element = element(type);
        return wrapForm(null);
    }

    @Override
    public <T> JForm<T> wrap(Class<T> type, T instance) {
        this.element = element(type);
        return wrapForm(instance);
    }

    @Override
    public <T> JForm<T> bind(Class<T> type) {
        this.element = element(type);
        return bindForm(null);
    }

    @Override
    public <T> JForm<T> bind(Class<T> type, T instance) {
        this.element = element(type);
        return bindForm(instance);
    }

    @Override
    public JForm<?> bind(Element element) {
        this.element = element;
        return bindForm(null);
    }

    // -- Instance

    private <T> ElementRef immutableRef(final T value) {
        ElementAccessor accessor = new ElementAccessor() {

            @Override
            public void set(Object value) {
                throw new IllegalStateException("Immutable reference");
            }

            @Override
            public Object get() {
                return value;
            }
        };
        return new ElementRef(element, accessor);
    }

    private <T> JForm<T> wrapForm(T initialValue) {
        FormNode unnamed = new FormNode(Node.createPrefix(null));

        try {
            FormNode root = unnamed.getDefinedChild(Path.createPath(path));

            return new Form<T>(factory, initialValue, immutableRef(initialValue), root);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse root path " + path);
        }
    }

    private <T> JForm<T> bindForm(T initialValue) {
        FormNode unnamed = parse(params);

        try {
            FormNode root = unnamed.getDefinedChild(Path.createPath(path));

            LOGGER.trace(printTree(root));

            T value = bind(root, initialValue);
            LOGGER.trace(printTree(root));

            validate(root, value);
            LOGGER.trace(printTree(root));

            return new Form<T>(factory, value, immutableRef(value), root);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse root path " + path);
        }
    }

    private static FormNode parse(Map<String, List<String>> params) {
        FormNode unnamed = new FormNode(Node.createPrefix(null));

        for (Entry<String, List<String>> entry : params.entrySet()) {
            String param = entry.getKey();
            List<String> values = entry.getValue();

            try {
                Path path = Path.createPath(param);
                FormNode node = unnamed.getDefinedChild(path);
                node.setStringValues(values);
            } catch (ParseException e) {
                LOGGER.warn("Cannot parse parameter name {}", param);
            }
        }

        return unnamed;
    }

    @SuppressWarnings("unchecked")
    private <T> T bind(FormNode root, T initialValue) {
        BinderFactory binderFactory = factory.getBinderFactory();

        ElementAccessor accessor = new ElementAccessor() {
            private T value;

            @Override
            public void set(Object value) {
                this.value = (T) value;
            }

            @Override
            public Object get() {
                return value;
            }
        };
        ElementRef ref = new ElementRef(element, accessor);

        accessor.set(initialValue);

        Binder binder = binderFactory.getBinder(ref, root.hasChildren());
        binder.read(root);
        root.setBinder(binder);

        return (T) accessor.get();
    }

    private <T> void validate(FormNode root, T value) {
        if (DISABLE_VALIDATION) {
            return;
        }

        ValidatorFactory validatorFactory = factory.getValidatorFactory();
        if (validatorFactory == null) {
            return;
        }

        Validator validator = validatorFactory.getValidator();

        // https://hibernate.onjira.com/browse/HV-549

        validateType(validator, root, value);
    }

    private <T> void validateType(Validator validator, FormNode root, T value) {
        Set<ConstraintViolation<Object>> violations = ValidatorExt.validateType(validator, element, value);

        for (ConstraintViolation<Object> violation : violations) {
            LOGGER.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }

        for (ConstraintViolation<Object> violation : violations) {
            javax.validation.Path validationPath = violation.getPropertyPath();
            String param = validationPath.toString();

            try {
                // parse and normalize the validation property path
                Path path = Path.createPath(param);

                // annotate the form tree with violations
                FormNode node = root.getDefinedChild(path);
                node.addConstraintViolation(violation);
            } catch (ParseException e) {
                throw new IllegalStateException("Cannot parse validation path " + param);
            }
        }
    }

    private static String printTree(FormNode root) {
        StringBuilder sb = new StringBuilder("\n");
        root.printTree(0, sb);
        return sb.toString();
    }

}
