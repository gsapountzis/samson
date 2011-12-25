package samson.form;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.Element;
import samson.convert.ConverterException;
import samson.form.Property.Path;

/**
 * Wrapping form.
 */
class WrapForm<T> extends AbstractForm<T> {

    public WrapForm(Element parameter, T parameterValue) {
        super(parameter, parameterValue);
    }

    // -- Form methods

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public Set<ConverterException> getConversionErrors() {
        return Collections.emptySet();
    }

    @Override
    public Set<ConstraintViolation<T>> getConstraintViolations() {
        return Collections.emptySet();
    }

    // -- Field methods

    @Override
    public Field getField(final String param) {
        final Path path = Path.createPath(param);
        final FormNode root = formPath(path);
        final FormNode node = root.getDefinedChild(path);

        return new Field() {

            @Override
            public Element getElement() {
                return node.getElement();
            }

            @Override
            public Object getObjectValue() {
                return node.getObjectValue();
            }

            @Override
            public String getValue() {
                return node.getValue(form);
            }

            @Override
            public List<String> getValues() {
                return node.getValues(form);
            }

            @Override
            public boolean isError() {
                return false;
            }

            @Override
            public ConverterException getConversionError() {
                return null;
            }

            @Override
            public Set<ConstraintViolation<?>> getConstraintViolations() {
                return Collections.emptySet();
            }

            @Override
            public Messages getMessages() {
                return form.getMessages(param);
            }

        };
    }

    @Override
    public Messages getMessages(final String param) {

        return new Messages() {

            @Override
            public String getConversionInfo() {
                return getDefaultConversionInfo(param);
            }

            @Override
            public String getConversionError() {
                return null;
            }

            @Override
            public List<String> getValidationInfos() {
                return getDefaultValidationInfos(param);
            }

            @Override
            public List<String> getValidationErrors() {
                return Collections.emptyList();
            }

            @Override
            public List<String> getInfos() {
                return getMessages(infos, param);
            }

            @Override
            public List<String> getErrors() {
                return getMessages(errors, param);
            }

        };
    }

}
