package samson.form;

import java.lang.annotation.Annotation;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.JForm;
import samson.JFormBuilder;
import samson.JFormProvider;
import samson.bind.BinderFactory;
import samson.convert.ConverterProvider;
import samson.metadata.Element;

public class FormFactory implements JFormProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormFactory.class);

    private final ParamsProvider formParams;
    private final ParamsProvider queryParams;

    private final BinderFactory binderFactory;
    private final ValidatorFactory validatorFactory;

    public FormFactory(ParamsProvider formParams, ParamsProvider queryParams, ConverterProvider converterProvider) {
        this.formParams = formParams;
        this.queryParams = queryParams;

        this.binderFactory = new BinderFactory(converterProvider);

        ValidatorFactory validatorFactory = null;
        try {
            validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        catch (ValidationException ex) {
            LOGGER.warn("Unable to find validation provider", ex);
        }
        this.validatorFactory = validatorFactory;
    }

    ParamsProvider getFormParams() {
        return formParams;
    }

    ParamsProvider getQueryParams() {
        return queryParams;
    }

    BinderFactory getBinderFactory() {
        return binderFactory;
    }

    ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    // -- Binding form factory methods

    @Override
    public <T> JFormBuilder<T> bind(Class<T> type) {
        return bind(element(type), null);
    }

    @Override
    public <T> JFormBuilder<T> bind(Class<T> type, T instance) {
        return bind(element(type), instance);
    }

    @Override
    public JFormBuilder<?> bind(Element element) {
        return bind(element, null);
    }

    private <T> JFormBuilder<T> bind(Element element, T instance) {
        return builder(element, instance);
    }

    // -- Wrapping form factory methods

    @Override
    public <T> JForm<T> wrap(Class<T> type) {
        return wrap(element(type), null);
    }

    @Override
    public <T> JForm<T> wrap(Class<T> type, T instance) {
        return wrap(element(type), instance);
    }

    @Override
    public JForm<?> wrap(Element element) {
        return wrap(element, null);
    }

    private <T> JForm<T> wrap(Element element, T instance) {
        return builder(element, instance).wrap();
    }

    // -- Helpers

    private <T> Element element(Class<T> type) {
        Annotation[] annotations = new Annotation[0];
        return new Element(annotations, type, type, null);
    }

    private <T> FormBuilder<T> builder(Element element, T instance) {
        FormBuilder<T> form = new FormBuilder<T>(this, element, instance);
        return form;
    }

}
