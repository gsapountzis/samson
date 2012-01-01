package samson.form;

import static samson.Configuration.DISABLE_VALIDATION;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.JForm;
import samson.bind.Binder;
import samson.form.Property.Path;
import samson.metadata.Element;

class BindForm<T> extends Form<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BindForm.class);

    BindForm(Element parameter, T value, FormNode root) {
        super(parameter, value, root);
        LOGGER.trace(printTree());
    }

    JForm<T> apply() {
        Binder binder = binderFactory.getBinder(rootRef, rootNode.hasChildren());
        binder.read(rootNode);
        rootNode.setBinder(binder);
        LOGGER.trace(printTree());

        validate();
        LOGGER.trace(printTree());
        return this;
    }

    /**
     * Validate form value.
     * <p>
     * Validation of parameter with standard types (primitives, string, list,
     * map) actually requires method validation. We validate for beans as
     * expected and strings in case of user-defined types that may be beans.
     */
    private void validate() {
        if (DISABLE_VALIDATION) {
            return;
        }
        if (validatorFactory == null) {
            return;
        }
        if (rootValue == null) {
            return;
        }

        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(rootValue);

        for (ConstraintViolation<T> violation : constraintViolations) {
            LOGGER.debug("{}: {}", violation.getPropertyPath(), violation.getMessage());
        }

        for (ConstraintViolation<T> violation : constraintViolations) {
            // parse and normalize the validation property path
            javax.validation.Path validationPath = violation.getPropertyPath();
            String param = validationPath.toString();
            Path path = Path.createPath(param);

            // annotate the form tree with violations
            FormNode node = rootNode.getDefinedChild(path);
            node.addConstraintViolation(violation);
        }
    }

    private String printTree() {
        StringBuilder sb = new StringBuilder("\n");
        rootNode.printTree(0, sb);
        return sb.toString();
    }

}
