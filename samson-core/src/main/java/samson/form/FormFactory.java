package samson.form;

import java.util.List;
import java.util.Map;

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

    BinderFactory getBinderFactory() {
        return binderFactory;
    }

    ValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    // -- Path

    private FormBuilder builder(String path) {
        return new FormBuilder(this, path);
    }

    @Override
    public JFormBuilder path() {
        return builder(null);
    }

    @Override
    public JFormBuilder path(String path) {
        return builder(path);
    }

    // -- Params

    private FormBuilder builder(String path, Map<String, List<String>> params) {
        return new FormBuilder(this, path, params);
    }

    @Override
    public JFormBuilder params(Map<String, List<String>> params) {
        return builder(null, params);
    }

    @Override
    public JFormBuilder params(String path, Map<String, List<String>> params) {
        return builder(path, params);
    }

    @Override
    public JFormBuilder form() {
        return builder(null, formParams.get());
    }

    @Override
    public JFormBuilder form(String path) {
        return builder(path, formParams.get());
    }

    @Override
    public JFormBuilder query() {
        return builder(null, queryParams.get());
    }

    @Override
    public JFormBuilder query(String path) {
        return builder(path, queryParams.get());
    }

    // -- Wrap

    @Override
    public <T> JForm<T> wrap(Class<T> type) {
        return builder(null).wrap(type);
    }

    @Override
    public <T> JForm<T> wrap(Class<T> type, T instance) {
        return builder(null).wrap(type, instance);
    }

}
