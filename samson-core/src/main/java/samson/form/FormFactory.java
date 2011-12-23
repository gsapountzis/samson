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
import samson.convert.MultivaluedConverterProvider;
import samson.convert.ConverterPredicate;
import samson.metadata.Element;
import samson.metadata.TypeClassPair;

public class FormFactory implements JFormProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(FormFactory.class);

    private final ParamsProvider formParams;
    private final ParamsProvider queryParams;

    private final BinderFactory binderFactory;
    private final ValidatorFactory validatorFactory;
    private MultivaluedConverterProvider extractorProvider;

    public FormFactory() {
        this(null, null);
    }

    public FormFactory(ParamsProvider formParams, ParamsProvider queryParams) {
        this.formParams = formParams;
        this.queryParams = queryParams;

        this.binderFactory = new BinderFactory();

        ValidatorFactory validatorFactory = null;
        try {
            validatorFactory = Validation.buildDefaultValidatorFactory();
        }
        catch (ValidationException ex) {
            LOGGER.warn("Unable to find validation provider", ex);
            validatorFactory = null;
        }
        this.validatorFactory = validatorFactory;
    }

    // -- Setter injection for Jersey's custom DI

    public void setConverterProvider(ConverterProvider converterProvider) {
        binderFactory.setConverterProvider(converterProvider);
    }

    public void setExtractorProvider(MultivaluedConverterProvider extractorProvider) {
        this.extractorProvider = extractorProvider;
    }

    public void setStringTypePredicate(ConverterPredicate stringTypePredicate) {
        binderFactory.setStringTypePredicate(stringTypePredicate);
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

    public <T> JFormBuilder<T> bind(Element element, T instance) {
        FormBuilder<T> form = new FormBuilder<T>(element, instance);
        form.setBinderFactory(binderFactory);
        form.setValidatorFactory(validatorFactory);
        form.setExtractorProvider(extractorProvider);

        form.setFormParamsProvider(formParams);
        form.setQueryParamsProvider(queryParams);
        return form;
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

    public <T> JForm<T> wrap(Element element, T instance) {
        WrapForm<T> form = new WrapForm<T>(element, instance);
        form.setBinderFactory(binderFactory);
        form.setValidatorFactory(validatorFactory);
        form.setExtractorProvider(extractorProvider);
        return form;
    }

    private <T> Element element(Class<T> type) {
        Annotation[] annotations = new Annotation[0];
        TypeClassPair tcp = new TypeClassPair(type, type);
        return new Element(annotations, tcp, null);
    }
}
