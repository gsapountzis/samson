package samson.form;

import java.util.List;
import java.util.Map;

import javax.validation.ValidatorFactory;

import samson.JForm;
import samson.JFormBuilder;
import samson.bind.BinderFactory;
import samson.convert.ConverterProvider;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.Element;

class FormBuilder<T> implements JFormBuilder<T> {

    private final Element element;
    private final T instance;

    private ConverterProvider converterProvider;
    private BinderFactory binderFactory;
    private ValidatorFactory validatorFactory;

    private ParamsProvider formParams;
    private ParamsProvider queryParams;

    FormBuilder(Element element, T instance) {
        this.element = element;
        this.instance = instance;
    }

    public void setConverterProvider(ConverterProvider converterProvider) {
        this.converterProvider = converterProvider;
    }

    public void setBinderFactory(BinderFactory binderFactory) {
        this.binderFactory = binderFactory;
    }

    public void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
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
        return bind(path, params);
    }

    @Override
    public JForm<T> form() {
        return form(null);
    }

    @Override
    public JForm<T> form(String path) {
        return bind(path, formParams.get());
    }

    @Override
    public JForm<T> query() {
        return query(null);
    }

    @Override
    public JForm<T> query(String path) {
        return bind(path, queryParams.get());
    }

    private JForm<T> bind(String path, Map<String, List<String>> params) {
        FormNode root = parse(path, params);

        BindForm<T> form = new BindForm<T>(element, instance, root);
        form.setConverterProvider(converterProvider);
        form.setBinderFactory(binderFactory);
        form.setValidatorFactory(validatorFactory);

        return form.apply();
    }

    JForm<T> wrap() {
        WrapForm<T> form = new WrapForm<T>(element, instance);
        form.setConverterProvider(converterProvider);
        form.setBinderFactory(binderFactory);
        form.setValidatorFactory(validatorFactory);

        return form;
    }

    private FormNode parse(String rootPath, Map<String, List<String>> params) {

        FormNode unnamedRoot = new FormNode(Node.createPrefix(null));

        for (String param : params.keySet()) {
            List<String> values = params.get(param);

            Path path = Path.createPath(param);
            if (!path.isEmpty()) {
                FormNode node = unnamedRoot.getDefinedChild(path);
                node.setStringValues(values);
            }
        }

        return unnamedRoot.getDefinedChild(Path.createPath(rootPath));
    }

}
