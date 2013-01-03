package samson.form;

import java.util.List;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.bind.BinderFactory;
import samson.convert.ConverterProvider;
import samson.convert.multivalued.MultivaluedConverterProvider;

public class FormProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormProvider.class);

    private final ParamsProvider formParams;
    private final ParamsProvider queryParams;

    private final BinderFactory binderFactory;
    private final ValidatorFactory validatorFactory;

    public FormProvider(
            ParamsProvider formParams,
            ParamsProvider queryParams,
            ConverterProvider converterProvider,
            MultivaluedConverterProvider multivaluedConverterProvider)
    {
        this.formParams = formParams;
        this.queryParams = queryParams;

        this.binderFactory = new BinderFactory(converterProvider, multivaluedConverterProvider);

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

    public FormBuilder path() {
        return builder(null);
    }

    public FormBuilder path(String path) {
        return builder(path);
    }

    // -- Params

    private FormBuilder builder(String path, Map<String, List<String>> params) {
        return new FormBuilder(this, path, params);
    }

    public FormBuilder params(Map<String, List<String>> params) {
        return builder(null, params);
    }

    public FormBuilder params(String path, Map<String, List<String>> params) {
        return builder(path, params);
    }

    public FormBuilder form() {
        return builder(null, formParams.get());
    }

    public FormBuilder form(String path) {
        return builder(path, formParams.get());
    }

    public FormBuilder query() {
        return builder(null, queryParams.get());
    }

    public FormBuilder query(String path) {
        return builder(path, queryParams.get());
    }

    // -- Wrap

    public <T> SamsonForm<T> wrap(Class<T> type) {
        return builder(null).wrap(type);
    }

    public <T> SamsonForm<T> wrap(Class<T> type, T instance) {
        return builder(null).wrap(type, instance);
    }

}
