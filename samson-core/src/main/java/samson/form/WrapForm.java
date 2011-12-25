package samson.form;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.Element;
import samson.metadata.ElementRef;

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
    public Set<Throwable> getConversionErrors() {
        return Collections.emptySet();
    }

    @Override
    public Set<ConstraintViolation<T>> getConstraintViolations() {
        return Collections.emptySet();
    }

    // -- Field methods

    @Override
    public Field getField(final String param) {
        final ElementRef ref = getElementRef(param);
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
                    return toStringValue(element, elementValue);
                }
                else {
                    return null;
                }
            }

            @Override
            public List<String> getValues() {
                if (ref != ElementRef.NULL_REF) {
                    return toStringList(element, elementValue);
                }
                else {
                    return Collections.emptyList();
                }
            }

            @Override
            public boolean isError() {
                return false;
            }

            @Override
            public Throwable getConversionError() {
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
