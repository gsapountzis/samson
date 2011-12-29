package samson.form;

import static samson.JForm.Configuration.DISABLE_VALIDATION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import samson.metadata.ElementRef;

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
        Map<String, ConverterException> conversionErrors = new HashMap<String, ConverterException>();
        convert(root, "", conversionErrors);

        if (!conversionErrors.isEmpty()) {
            hasErrors = true;
        }

        for (Entry<String, ConverterException> entry : conversionErrors.entrySet()) {
            String param = entry.getKey();
            ConverterException conversionError = entry.getValue();
            LOGGER.debug("{} : {}", param, conversionError);
        }
        LOGGER.trace(printTree(root));
    }

    private void convert(FormNode node, String parent, Map<String, ConverterException> conversionErrors) {
        convertNode(node);

        String path = parent + node.getNode();
        if (node.isConversionError()) {
            conversionErrors.put(path, node.getConversionError());
        }

        for (FormNode child : node.getChildren()) {
            convert(child, path, conversionErrors);
        }
    }

    private void convertNode(FormNode node) {
        Binder binder = node.getBinder();
        if (binder.getType() == BinderType.STRING) {
            ElementRef ref = binder.getRef();
            List<String> stringValues = node.getStringValues();

            Conversion conversion = form.fromStringList(ref.element, stringValues);
            if (conversion != null) {
                if (conversion.isError()) {
                    node.setConversionError(conversion.getCause());
                }
                else {
                    ref.accessor.set(conversion.getValue());
                }
            }
        }
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

        if (!constraintViolations.isEmpty()) {
            hasErrors = true;
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
        root.printTree(sb, 0);
        return sb.toString();
    }

}
