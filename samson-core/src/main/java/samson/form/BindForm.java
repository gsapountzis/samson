package samson.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderNode;
import samson.bind.BinderType;
import samson.convert.Conversion;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.Element;
import samson.metadata.ElementRef;

/**
 * Binding form.
 */
class BindForm<T> extends AbstractForm<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BindForm.class);

    private static final boolean VALIDATE = true;

    private FormNode unnamedRoot;
    private FormNode root;

    private List<Conversion> conversionErrors = Collections.emptyList();
    private Set<ConstraintViolation<T>> violations = Collections.emptySet();

    public BindForm(Element parameter, T value) {
        super(parameter);
        this.value = value;
    }

    public JForm<T> apply(String path, MultivaluedMap<String, String> params) {

        parse(path, params);

        bind();

        validate();

        return this;
    }

    private static String printTree(FormNode root) {
        StringBuilder sb = new StringBuilder("\n");
        root.print(sb, 0);
        return sb.toString();
    }

    private void parse(String rootPath, MultivaluedMap<String, String> params) {

        unnamedRoot = new FormNode(Node.createPrefix(null));

        for (String param : params.keySet()) {
            List<String> values = params.get(param);

            Path path = Path.createPath(param);
            if (!path.isEmpty()) {
                FormNode node = unnamedRoot.getDefinedChild(path);
                node.setStringValues(values);
            }
        }

        root = unnamedRoot.getDefinedChild(Path.createPath(rootPath));

        LOGGER.trace(printTree(root));
    }

    private void convert(BinderNode binderNode, FormNode formNode) {
        Binder binder = binderNode.getBinder();

        if (binder.getType() == BinderType.STRING) {
            ElementRef ref = binder.getElementRef();

            Conversion conversion = fromStringList(ref.element, binderNode.getStringValues());
            formNode.setConversion(conversion);
            if (conversion.isError()) {
                conversionErrors.add(conversion);
            }
            else {
                ref.accessor.set(conversion.getValue());
            }
        }
        else {
            for (BinderNode binderChild : binderNode.getChildren()) {
                String name = binderChild.getName();
                convert(binderChild, formNode.getChild(name));
            }
        }
    }

    private void bind() {

        ElementRef ref = new ElementRef(parameter, valueAccessor);

        Binder binder = binderFactory.getBinder(ref, root.hasChildren());
        binder.read(root);

        conversionErrors = new ArrayList<Conversion>();
        convert(binder.getNode(), root);

        for (Conversion conversion : conversionErrors) {
            LOGGER.debug("conversion error cause {}", conversion.getCause().toString());
        }

        LOGGER.trace(printTree(root));
    }

    private void validate() {

        if (!VALIDATE || (validatorFactory == null)) {
            return;
        }

        Validator validator = validatorFactory.getValidator();

        if (value != null) {
            violations = validator.validate(value);
        }

        for (ConstraintViolation<T> violation : violations) {
            LOGGER.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }

        // annotate the parameter tree with violations
        for (ConstraintViolation<T> violation : violations) {
            // parse and normalize the validation property path
            javax.validation.Path validationPath = violation.getPropertyPath();
            String param = validationPath.toString();
            Path path = Path.createPath(param);
            FormNode node = root.getDefinedChild(path);

            node.getViolations().add(violation);
        }

        LOGGER.trace(printTree(root));
    }

    // -- Form methods

    @Override
    public boolean hasErrors() {
        if (conversionErrors.size() > 0) {
            return true;
        }
        if (violations.size() > 0) {
            return true;
        }
        if (!errors.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public List<Conversion> getConversionErrors() {
        return conversionErrors;
    }

    @Override
    public Set<ConstraintViolation<T>> getViolations() {
        return violations;
    }

    // -- Field methods

    @Override
    public Field getField(final String param) {
        final Path path = Path.createPath(param);
        final FormNode node = root.getDefinedChild(path);
        final Conversion binding = node.getConversion();

        return new Field() {

            @Override
            public String getName() {
                return param;
            }

            @Override
            public Object getObjectValue() {
                if (binding == null) {
                    return null;
                }

                return binding.getValue();
            }

            @Override
            public String getValue() {
                if (binding == null) {
                    return null;
                }

                if (binding.isError()) {
                    return Utils.getFirst(node.getStringValues());
                }
                else {
                    return toStringValue(binding.getElement(), binding.getValue());
                }
            }

            @Override
            public List<String> getValues() {
                if (binding == null) {
                    return null;
                }

                if (binding.isError()) {
                    return node.getStringValues();
                }
                else {
                    return toStringList(binding.getElement(), binding.getValue());
                }
            }

            @Override
            public boolean isError() {
                if (node.isError()) {
                    return true;
                }
                if (errors.containsKey(path)) {
                    return true;
                }
                return false;
            }

            @Override
            public Conversion getConversion() {
                return node.getConversion();
            }

            @Override
            public Set<ConstraintViolation<?>> getViolations() {
                return node.getViolations();
            }

            @Override
            public Messages getMessages() {
                return form.getMessages(param);
            }

        };
    }

    @Override
    public Messages getMessages(final String param) {
        final Field field = getField(param);
        final Messages messages = super.getMessages(param);

        return new Messages() {

            @Override
            public String getConversionInfo() {
                return messages.getConversionInfo();
            }

            @Override
            public String getConversionError() {
                Conversion binding = field.getConversion();
                if (binding == null) {
                    return null;
                }

                if (binding.isError()) {
                    String stringValue = field.getValue();
                    return getErrorMessage(binding.getElement(), stringValue);
                }
                return null;
            }

            @Override
            public List<String> getValidationInfos() {
                return messages.getValidationInfos();
            }

            @Override
            public List<String> getValidationErrors() {
                List<String> messages = new ArrayList<String>();
                for (ConstraintViolation<?> violation : field.getViolations()) {
                    messages.add(violation.getMessage());
                }
                return messages;
            }

            @Override
            public List<String> getInfos() {
                return messages.getInfos();
            }

            @Override
            public List<String> getErrors() {
                return messages.getErrors();
            }

        };
    }

    private final static String ERROR_MESSAGE_TEMPLATE = "cannot convert value '%s' to '%s'";

    private static String getErrorMessage(Element element, String value) {
        return String.format(ERROR_MESSAGE_TEMPLATE, value, element.tcp.c.getSimpleName());
    }

}
