package samson.form;

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

import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;
import samson.parse.Property.Node;
import samson.parse.Property.Path;

public class FormBuilder {

    private static final boolean DISABLE_BEAN_VALIDATION = false;

    private static final Logger LOGGER = LoggerFactory.getLogger(FormBuilder.class);

    private final FormProvider factory;
    private final String path;
    private final Map<String, List<String>> params;

    private Element element;

    FormBuilder(FormProvider factory, String path) {
        this(factory, path, null);
    }

    FormBuilder(FormProvider factory, String path, Map<String, List<String>> params) {
        this.factory = factory;
        this.path = path;
        this.params = params;
    }

    // -- Element

    private <T> Element element(Class<T> type) {
        Annotation[] annotations = new Annotation[0];
        return new Element(annotations, type, type, null);
    }

    public <T> SamsonForm<T> wrap(Class<T> type) {
        this.element = element(type);
        return wrapForm(null);
    }

    public <T> SamsonForm<T> wrap(Class<T> type, T instance) {
        this.element = element(type);
        return wrapForm(instance);
    }

    public <T> SamsonForm<T> bind(Class<T> type) {
        this.element = element(type);
        return bindForm(null);
    }

    public <T> SamsonForm<T> bind(Class<T> type, T instance) {
        this.element = element(type);
        return bindForm(instance);
    }

    public SamsonForm<?> bind(Element element) {
        this.element = element;
        return bindForm(null);
    }

    // -- Instance

    private static FormNode getNode(FormNode root, String param, boolean setRef) throws ParseException {
        Path path = Path.createPath(param);

        FormNode child = root;
        for (Node node : path) {
            child = child.path(node.getName(), setRef);
        }
        return child;
    }

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

    private <T> SamsonForm<T> wrapForm(T initialValue) {
        FormNode unnamed = new FormNode(factory, null);

        try {
            FormNode root = getNode(unnamed, path, false);
            root.setRef(immutableRef(initialValue));
            return new SamsonForm<T>(initialValue, root);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse root path " + path);
        }
    }

    private <T> SamsonForm<T> bindForm(T initialValue) {
        FormNode unnamed = parse(params);

        try {
            FormNode root = getNode(unnamed, path, false);
            LOGGER.trace(printTree(root));

            T value = bind(root, initialValue);
            LOGGER.trace(printTree(root));

            validate(root, value);
            LOGGER.trace(printTree(root));

            root.setRef(immutableRef(value));
            return new SamsonForm<T>(value, root);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse root path " + path);
        }
    }

    private FormNode parse(Map<String, List<String>> params) {
        FormNode unnamed = new FormNode(factory, null);

        for (Entry<String, List<String>> entry : params.entrySet()) {
            String param = entry.getKey();
            List<String> values = entry.getValue();

            try {
                FormNode node = getNode(unnamed, param, false);
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
        accessor.set(initialValue);

        ElementRef ref = new ElementRef(element, accessor);
        root.setRef(ref);

        Binder binder = binderFactory.getBinder(ref, root.hasChildren());
        binder.read(root);

        return (T) accessor.get();
    }

    private <T> void validate(FormNode root, T value) {
        if (DISABLE_BEAN_VALIDATION) {
            return;
        }

        ValidatorFactory validatorFactory = factory.getValidatorFactory();
        if (validatorFactory == null) {
            return;
        }

        Validator validator = validatorFactory.getValidator();

        Set<ConstraintViolation<Object>> violations = ValidatorExt.validateType(validator, element, value);

        addConstraintViolations(root, violations);
    }

    private static void addConstraintViolations(FormNode root, Set<ConstraintViolation<Object>> violations) {

        for (ConstraintViolation<Object> violation : violations) {
            LOGGER.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }

        for (ConstraintViolation<Object> violation : violations) {
            javax.validation.Path validationPath = violation.getPropertyPath();
            String param = validationPath.toString();

            try {
                // parse and normalize the validation property path
                FormNode node = getNode(root, param, true);
                // annotate the form tree with violations
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
