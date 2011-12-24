package samson.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.Conversion;
import samson.Element;
import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderType;
import samson.form.Property.Path;
import samson.metadata.ElementRef;

/**
 * Binding form.
 */
class BindForm<T> extends AbstractForm<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BindForm.class);

    private static final boolean VALIDATE = true;

    private final FormNode root;

    private List<Conversion> conversionErrors = Collections.emptyList();
    private Set<ConstraintViolation<T>> violations = Collections.emptySet();

    public BindForm(Element parameter, T parameterValue, FormNode root) {
        super(parameter, parameterValue);
        this.root = root;

        LOGGER.trace(printTree(root));
    }

    public JForm<T> apply() {

        bind();

        validate();

        return this;
    }

    private void bind() {
        ElementRef ref = new ElementRef(parameter, parameterAccessor);

        Binder binder = binderFactory.getBinder(ref, root.hasChildren());
        binder.read(root);
        root.setBinder(binder);

        conversionErrors = new ArrayList<Conversion>();
        convert(root);

        for (Conversion conversion : conversionErrors) {
            LOGGER.debug("conversion error cause {}", conversion.getCause().toString());
        }

        LOGGER.trace(printTree(root));
    }

    private void convert(FormNode node) {
        Binder binder = node.getBinder();
        if (binder == null) {
            return;
        }
        ElementRef ref = binder.getElementRef();

        if (binder.getType() == BinderType.STRING) {
            List<String> values = node.getStringValues();
            Conversion conversion = fromStringList(ref.element, values);
            node.setConversion(conversion);
            if (conversion.isError()) {
                conversionErrors.add(conversion);
            }
            else {
                ref.accessor.set(conversion.getValue());
            }
        }
        else {
            Conversion conversion = conversionFromElement(ref);
            node.setConversion(conversion);
            for (FormNode child : node.getChildren()) {
                convert(child);
            }
        }
    }

    private void validate() {
        if (!VALIDATE || (validatorFactory == null)) {
            return;
        }

        Validator validator = validatorFactory.getValidator();

        if (parameterValue != null) {
            violations = validator.validate(parameterValue);
        }

        for (ConstraintViolation<T> violation : violations) {
            LOGGER.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }

        for (ConstraintViolation<T> violation : violations) {
            // parse and normalize the validation property path
            javax.validation.Path validationPath = violation.getPropertyPath();
            String param = validationPath.toString();
            Path path = Path.createPath(param);

            // annotate the form tree with violations
            FormNode node = root.getDefinedChild(path);
            node.addViolation(violation);
        }

        LOGGER.trace(printTree(root));
    }

    private static String printTree(FormNode root) {
        StringBuilder sb = new StringBuilder("\n");
        root.print(sb, 0);
        return sb.toString();
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
                    return toStringValue(binding);
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
                    return toStringList(binding);
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
                return binding;
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
        final Messages messages = super.getMessages(param);
        final Path path = Path.createPath(param);
        final FormNode node = root.getDefinedChild(path);

        return new Messages() {

            @Override
            public String getConversionInfo() {
                return messages.getConversionInfo();
            }

            @Override
            public String getConversionError() {
                Conversion binding = node.getConversion();
                if (binding == null) {
                    return null;
                }

                if (binding.isError()) {
                    String stringValue = Utils.getFirst(node.getStringValues());
                    return getConversionErrorMessage(binding, stringValue);
                }
                return null;
            }

            @Override
            public List<String> getValidationInfos() {
                return messages.getValidationInfos();
            }

            @Override
            public List<String> getValidationErrors() {
                Set<ConstraintViolation<?>> violations = node.getViolations();

                List<String> messages = new ArrayList<String>();
                for (ConstraintViolation<?> violation : violations) {
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

    private final static String CONVERSION_ERROR_MESSAGE_TEMPLATE = "cannot convert value '%s' to '%s'";

    private static String getConversionErrorMessage(Conversion conversion, String value) {
        return String.format(CONVERSION_ERROR_MESSAGE_TEMPLATE, value, conversion.getRawType().getSimpleName());
    }

}
