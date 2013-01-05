package samson.form;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.bind.AnyNode;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.bind.TypedNode;
import samson.bind.UntypedNode;
import samson.metadata.Element;
import samson.metadata.TypeClassPair;

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
        TypeClassPair tcp = new TypeClassPair(type, type);
        return new Element(Element.NO_ANNOTATIONS, tcp);
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

    // -- Form Instance

    private <T> SamsonForm<T> wrapForm(T initialValue) {
        TypedNode anyRoot = new AnyNode(element, initialValue);

        FormNode root = new FormNode(factory, "ROOT", anyRoot);
        LOGGER.trace(printTree(root));

        return new SamsonForm<T>(root, initialValue);
    }

    @SuppressWarnings("unchecked")
    private <T> SamsonForm<T> bindForm(T initialValue) {

        UntypedNode unnamed = UntypedNode.parse(params);
        UntypedNode untypedRoot;
        try {
            untypedRoot = UntypedNode.getNode(unnamed, path);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse root path " + path, e);
        }

        BinderFactory binderFactory = factory.getBinderFactory();
        Binder binder = binderFactory.getBinder(element, untypedRoot.hasChildren());
        TypedNode typedRoot = binder.parse(untypedRoot, initialValue);

        T value = (T) typedRoot.getObject();

        FormNode root = new FormNode(factory, "ROOT", typedRoot);
        LOGGER.trace(printTree(root));

        validate(root, value);
        LOGGER.trace(printTree(root));

        return new SamsonForm<T>(root, value);
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
                FormNode node = FormNode.getNode(root, param);
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
