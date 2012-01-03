package samson.form;

import static samson.Configuration.DISABLE_VALIDATION;

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

class FormBuilder<T> implements JFormBuilder<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormBuilder.class);

    private final FormFactory factory;

    private final Element element;
    private final T initialValue;

    FormBuilder(FormFactory factory, Element element, T instance) {
        this.factory = factory;
        this.element = element;
        this.initialValue = instance;
    }

    private ElementRef immutableRef(final T value) {
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

    JForm<T> wrap() {
        FormNode root = new FormNode(Node.createPrefix(null));

        return new Form<T>(factory, initialValue, immutableRef(initialValue), root);
    }

    @Override
    public JForm<T> params(Map<String, List<String>> params) {
        return params(null, params);
    }

    @Override
    public JForm<T> params(String path, Map<String, List<String>> params) {
        return bind(path, params);
    }

    @Override
    public JForm<T> form() {
        return form(null);
    }

    @Override
    public JForm<T> form(String path) {
        return bind(path, factory.getFormParams().get());
    }

    @Override
    public JForm<T> query() {
        return query(null);
    }

    @Override
    public JForm<T> query(String path) {
        return bind(path, factory.getQueryParams().get());
    }

    private JForm<T> bind(String path, Map<String, List<String>> params) {
        FormNode root = parse(path, params);
        LOGGER.trace(printTree(root));

        final T value = bind(root);
        LOGGER.trace(printTree(root));

        validate(root, value);
        LOGGER.trace(printTree(root));

        return new Form<T>(factory, value, immutableRef(value), root);
    }

    private static FormNode parse(String rootPath, Map<String, List<String>> params) {

        FormNode unnamedRoot = new FormNode(Node.createPrefix(null));

        for (Entry<String, List<String>> entry : params.entrySet()) {
            String param = entry.getKey();
            List<String> values = entry.getValue();

            Path path = Path.createPath(param);
            if (!path.isEmpty()) {
                FormNode node = unnamedRoot.getDefinedChild(path);
                node.setStringValues(values);
            }
        }

        return unnamedRoot.getDefinedChild(Path.createPath(rootPath));
    }

    @SuppressWarnings("unchecked")
    private T bind(FormNode root) {
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

    private void validate(FormNode root, T value) {
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

    private void validateType(Validator validator, FormNode root, T value) {
        Set<ConstraintViolation<Object>> violations = ValidatorExt.validateType(validator, element, value);

        for (ConstraintViolation<Object> violation : violations) {
            LOGGER.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }

        for (ConstraintViolation<Object> violation : violations) {
            // parse and normalize the validation property path
            javax.validation.Path validationPath = violation.getPropertyPath();
            String param = validationPath.toString();
            Path path = Path.createPath(param);

            // annotate the form tree with violations
            FormNode node = root.getDefinedChild(path);
            node.addConstraintViolation(violation);
        }
    }

    private static String printTree(FormNode root) {
        StringBuilder sb = new StringBuilder("\n");
        root.printTree(0, sb);
        return sb.toString();
    }

}
