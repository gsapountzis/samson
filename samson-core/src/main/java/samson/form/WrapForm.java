package samson.form;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.Conversion;
import samson.metadata.Element;
import samson.metadata.ElementRef;

/**
 * Wrapping form.
 */
class WrapForm<T> extends AbstractForm<T> {

    public WrapForm(Element parameter, T value) {
        super(parameter);
        this.value = value;
    }

    // -- Form methods

    @Override
    public boolean hasErrors() {
        return false;
    }

    @Override
    public List<Conversion> getConversionErrors() {
        return Collections.emptyList();
    }

    @Override
    public Set<ConstraintViolation<T>> getViolations() {
        return Collections.emptySet();
    }

    // -- Field methods

    @Override
    public Field getField(final String param) {
        final ElementRef ref = getElementRef(param);
        final Conversion binding = conversionFromElement(ref);

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

                return toStringValue(binding);
            }

            @Override
            public List<String> getValues() {
                if (binding == null) {
                    return null;
                }

                return toStringList(binding);
            }

            @Override
            public boolean isError() {
                return false;
            }

            @Override
            public Conversion getConversion() {
                return binding;
            }

            @Override
            public Set<ConstraintViolation<?>> getViolations() {
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
        final Messages messages = super.getMessages(param);

        return new Messages() {

            @Override
            public String getConversionInfo() {
                return messages.getConversionInfo();
            }

            @Override
            public String getConversionError() {
                return null;
            }

            @Override
            public List<String> getValidationInfos() {
                return messages.getValidationInfos();
            }

            @Override
            public List<String> getValidationErrors() {
                return Collections.emptyList();
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

}
