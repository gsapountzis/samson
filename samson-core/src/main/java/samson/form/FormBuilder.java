package samson.form;

import java.util.List;
import java.util.Map;

import javax.validation.ValidatorFactory;

import samson.JForm;
import samson.JFormBuilder;
import samson.bind.BinderFactory;
import samson.convert.MultivaluedConverterProvider;
import samson.metadata.Element;

class FormBuilder<T> implements JFormBuilder<T> {

    private final Element element;
    private final T instance;

    private BinderFactory binderFactory;
    private ValidatorFactory validatorFactory;
    private MultivaluedConverterProvider extractorProvider;

    private ParamsProvider formParams;
    private ParamsProvider queryParams;

    FormBuilder(Element element, T instance) {
        this.element = element;
        this.instance = instance;
    }

    public void setBinderFactory(BinderFactory binderFactory) {
        this.binderFactory = binderFactory;
    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    public void setExtractorProvider(MultivaluedConverterProvider extractorProvider) {
        this.extractorProvider = extractorProvider;
    }

    public void setFormParamsProvider(ParamsProvider formParams) {
        this.formParams = formParams;
    }

    public void setQueryParamsProvider(ParamsProvider queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public JForm<T> params(Map<String, List<String>> params) {
        return params(null, params);
    }

    @Override
    public JForm<T> params(String path, Map<String, List<String>> params) {
        return apply(path, params);
    }

    @Override
    public JForm<T> form() {
        return form(null);
    }

    @Override
    public JForm<T> form(String path) {
        return apply(path, formParams.get());
    }

    @Override
    public JForm<T> query() {
        return query(null);
    }

    @Override
    public JForm<T> query(String path) {
        return apply(path, queryParams.get());
    }

    private JForm<T> apply(String path, Map<String, List<String>> params) {
        BindForm<T> form = new BindForm<T>(element, instance);
        form.setBinderFactory(binderFactory);
        form.setValidatorFactory(validatorFactory);
        form.setExtractorProvider(extractorProvider);

        return form.apply(path, params);
    }

}
