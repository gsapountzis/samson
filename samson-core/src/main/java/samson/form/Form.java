package samson.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import samson.JForm;
import samson.bind.BinderFactory;
import samson.convert.ConverterProvider;
import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;

class Form<T> implements JForm<T> {

    protected final Form<T> form = this;
    protected final FormNode rootNode;
    protected final FormField rootField;

    protected final Element rootElement;
    protected final ElementAccessor rootAccessor;
    protected final ElementRef rootRef;

    protected T rootValue;

    protected ConverterProvider converterProvider;
    protected BinderFactory binderFactory;
    protected ValidatorFactory validatorFactory;

    Form(Element element, T value, FormNode root) {
        this.rootElement = element;
        this.rootValue = value;
        this.rootAccessor = new ElementAccessor() {

            @Override
            public Object get() {
                return form.rootValue;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void set(Object value) {
                form.rootValue = (T) value;
            }

        };
        this.rootRef = new ElementRef(rootElement, rootAccessor);
        this.rootNode = root;
        this.rootField = new FormField(form, rootRef, rootNode);
    }

    void setConverterProvider(ConverterProvider converterProvider) {
        this.converterProvider = converterProvider;
    }

    void setBinderFactory(BinderFactory binderFactory) {
        this.binderFactory = binderFactory;
    }

    void setValidatorFactory(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    // -- Path

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public JForm<?> path(String path) {
        return new PathForm(this, path);
    }

    @Override
    public JForm<?> dot(String property) {
        return new PathForm(this, property);
    }

    @Override
    public JForm<?> index(String index) {
        return new PathForm(this, "[" + index + "]");
    }

    @Override
    public JForm<?> index(int index) {
        return index(Integer.toString(index));
    }

    // -- Form

    @Override
    public T get() {
        return rootValue;
    }

    ElementRef getRef() {
        return rootRef;
    }

    FormNode getNode() {
        return rootNode;
    }

    @Override
    public boolean hasErrors() {
        return rootNode.isTreeError();
    }

    @Override
    public Map<String, List<String>> getInfos() {
        return getInfos(null, rootNode);
    }

    @Override
    public Map<String, List<String>> getErrors() {
        return getErrors(null, rootNode);
    }

    Map<String, List<String>> getInfos(String param, FormNode node) {
        Map<String, List<String>> infos = new HashMap<String, List<String>>();
        node.getTreeInfos(param, infos);
        return infos;
    }

    Map<String, List<String>> getErrors(String param, FormNode node) {
        Map<String, List<String>> errors = new HashMap<String, List<String>>();
        node.getTreeErrors(param, errors);
        return errors;
    }

    // -- Field

    @Override
    public Field getField() {
        return rootField;
    }

    @Override
    public Messages getMessages() {
        return rootField;
    }

    @Override
    public void info(String msg) {
        rootNode.info(msg);
    }

    @Override
    public void error(String msg) {
        rootNode.error(msg);
    }

    // -- Callbacks for access to conversion, validation services

    BinderFactory getBinderFactory() {
        return binderFactory;
    }

    Validator getValidator() {
        return validatorFactory.getValidator();
    }

}
