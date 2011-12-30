package samson.form;

import static samson.Configuration.DISABLE_VALIDATION;

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

class BindForm<T> extends Form<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BindForm.class);

    public BindForm(FormNode root, Element parameter, T parameterValue) {
        super(root, parameter, parameterValue);
        LOGGER.trace(printTree());
    }

    public JForm<T> apply() {
        Binder binder = binderFactory.getBinder(parameterRef, root.hasChildren());
        if (binder != Binder.NULL_BINDER) {
            BinderType binderType = binder.getType();
            binder.read(root);
            root.setBinder(binder);

            root.convertTree(form);
            validate(binderType);

            hasErrors = root.isTreeError();
            LOGGER.trace(printTree());
        }
        return this;
    }

    /**
     * Validate form value.
     * <p>
     * Validation of parameter with standard types (primitives, string, list,
     * map) actually requires method validation. We validate for beans as
     * expected and strings in case of user-defined types that may be beans.
     */
    private void validate(BinderType binderType) {
        if (DISABLE_VALIDATION) {
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
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(parameterValue);

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
    }

    private String printTree() {
        StringBuilder sb = new StringBuilder("\n");
        root.printTree(sb, 0);
        return sb.toString();
    }

}
