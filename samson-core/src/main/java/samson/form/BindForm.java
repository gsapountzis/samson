package samson.form;

import java.util.HashSet;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.Element;
import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderType;
import samson.convert.ConverterException;
import samson.form.Property.Path;

class BindForm<T> extends Form<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BindForm.class);

    public BindForm(FormNode root, Element parameter, T parameterValue) {
        super(root, parameter, parameterValue);
        LOGGER.trace(printTree(root));
    }

    public JForm<T> apply() {
        Binder binder = binderFactory.getBinder(parameterRef, root.hasChildren());
        if (binder != Binder.NULL_BINDER) {
            BinderType binderType = binder.getType();

            binder.read(root);
            root.setBinder(binder);

            convert();

            validate(binderType);
        }
        return this;
    }

    private void convert() {
        root.convertTree(form);

        conversionErrors = new HashSet<ConverterException>();
        root.conversionErrors(conversionErrors);

        for (ConverterException conversionError : conversionErrors) {
            LOGGER.debug("conversion error cause {}", conversionError.toString());
        }
        LOGGER.trace(printTree(root));
    }

    /**
     * Validate form value.
     * <p>
     * Validation of parameter with standard types (primitives, string, list,
     * map) actually requires method validation. We validate for beans as
     * expected and strings in case of user-defined types that may be beans.
     */
    private void validate(BinderType binderType) {
        if (JForm.CONF_DISABLE_VALIDATION) {
            return;
        }
        if (validatorFactory == null) {
            return;
        }
        if (parameterValue == null) {
            return;
        }

        if ((binderType != BinderType.STRING) && (binderType != BinderType.BEAN)) {
            return;
        }

        Validator validator = validatorFactory.getValidator();
        constraintViolations = validator.validate(parameterValue);

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
        root.printTree(sb, 0);
        return sb.toString();
    }

}
