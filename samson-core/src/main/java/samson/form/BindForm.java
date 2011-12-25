package samson.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private Set<Throwable> conversionErrors = Collections.emptySet();
    private Set<ConstraintViolation<T>> constraintViolations = Collections.emptySet();

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

        conversionErrors = new HashSet<Throwable>();
        convert(root);

        for (Throwable conversionError : conversionErrors) {
            LOGGER.debug("conversion error cause {}", conversionError.toString());
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
            if (conversion.isError()) {
                node.setConversionError(conversion.getCause());
                conversionErrors.add(conversion.getCause());
            }
            else {
                ref.accessor.set(conversion.getValue());
            }
        }
        else {
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
            constraintViolations = validator.validate(parameterValue);
        }

        for (ConstraintViolation<T> violation : constraintViolations) {
            LOGGER.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }

        for (ConstraintViolation<T> violation : constraintViolations) {
            // parse and normalize the validation property path
            javax.validation.Path validationPath = violation.getPropertyPath();
            String param = validationPath.toString();
            Path path = Path.createPath(param);

            // annotate the form tree with violations
            FormNode node = root.getDefinedChild(path);
            node.addConstraintViolation(violation);
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
        if (constraintViolations.size() > 0) {
            return true;
        }
        if (!errors.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public Set<Throwable> getConversionErrors() {
        return conversionErrors;
    }

    @Override
    public Set<ConstraintViolation<T>> getConstraintViolations() {
        return constraintViolations;
    }

    // -- Field methods

    @Override
    public Field getField(final String param) {
        final Path path = Path.createPath(param);
        final FormNode node = root.getDefinedChild(path);

        final Binder binder = node.getBinder();
        final ElementRef ref = (binder != null) ? binder.getElementRef() : ElementRef.NULL_REF;
        final Element element = ref.element;
        final Object elementValue = ref.accessor.get();

        return new Field() {

            @Override
            public Element getElement() {
                if (ref != ElementRef.NULL_REF) {
                    return element;
                }
                else {
                    return null;
                }
            }

            @Override
            public Object getObjectValue() {
                if (ref != ElementRef.NULL_REF) {
                    return elementValue;
                }
                else {
                    return null;
                }
            }

            @Override
            public String getValue() {
                if (ref != ElementRef.NULL_REF) {
                    if (node.isError()) {
                        return Utils.getFirst(node.getStringValues());
                    }
                    else {
                        return toStringValue(element, elementValue);
                    }
                }
                else {
                    return null;
                }
            }

            @Override
            public List<String> getValues() {
                if (ref != ElementRef.NULL_REF) {
                    if (node.isError()) {
                        return node.getStringValues();
                    }
                    else {
                        return toStringList(element, elementValue);
                    }
                }
                else {
                    return Collections.emptyList();
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
            public Throwable getConversionError() {
                return node.getConversionError();
            }

            @Override
            public Set<ConstraintViolation<?>> getConstraintViolations() {
                return node.getConstraintViolations();
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
                Throwable conversionError = node.getConversionError();

                if (conversionError != null) {
                    String stringValue = Utils.getFirst(node.getStringValues());
                    return getConversionErrorMessage(stringValue);
                }
                return null;
            }

            @Override
            public List<String> getValidationInfos() {
                return messages.getValidationInfos();
            }

            @Override
            public List<String> getValidationErrors() {
                Set<ConstraintViolation<?>> constraintViolations = node.getConstraintViolations();

                List<String> messages = new ArrayList<String>();
                for (ConstraintViolation<?> violation : constraintViolations) {
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

    private final static String CONVERSION_ERROR_MESSAGE_TEMPLATE = "invalid value '%s'";

    private static String getConversionErrorMessage(String value) {
        return String.format(CONVERSION_ERROR_MESSAGE_TEMPLATE, value);
    }

}
