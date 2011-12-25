package samson.form;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.PropertyDescriptor;

import samson.Element;
import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.convert.ConverterException;
import samson.convert.ConverterProvider;
import samson.convert.MultivaluedConverter;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;

abstract class AbstractForm<T> implements JForm<T> {

    protected final AbstractForm<T> form = this;

    protected final Element parameter;
    protected final ElementAccessor parameterAccessor;
    protected final ElementRef parameterRef;

    protected T parameterValue;

    protected ConverterProvider converterProvider;
    protected BinderFactory binderFactory;
    protected ValidatorFactory validatorFactory;

    public AbstractForm(Element parameter, T parameterValue) {
        this.parameterValue = parameterValue;

        this.parameter = parameter;
        this.parameterAccessor = new ElementAccessor() {

            @Override
            public Object get() {
                return form.parameterValue;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void set(Object value) {
                form.parameterValue = (T) value;
            }

        };
        this.parameterRef = new ElementRef(parameter, parameterAccessor);
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

    @Override
    public T get() {
        return parameterValue;
    }

    // -- Path

    @Override
    public JForm<?> path(String path) {
        return new PathForm(this, path);
    }

    @Override
    public JForm<?> dot(String property) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JForm<?> index(String index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JForm<?> index(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPath() {
        return null;
    }

    // -- Messages

    final Map<Path, List<String>> infos = new HashMap<Path, List<String>>();
    final Map<Path, List<String>> errors = new HashMap<Path, List<String>>();

    @Override
    public void info(String msg) {
        info(null, msg);
    }

    @Override
    public void info(String path, String msg) {
        message(infos, path, msg);
    }

    @Override
    public void error(String msg) {
        error(null, msg);
    }

    @Override
    public void error(String path, String msg) {
        message(errors, path, msg);
    }

    static void message(Map<Path, List<String>> level, String param, String msg) {
        Path path = Path.createPath(param);

        List<String> msgs = level.get(path);
        if (msgs == null) {
            msgs = new ArrayList<String>();
            level.put(path, msgs);
        }
        msgs.add(msg);
    }

    static List<String> getMessages(Map<Path, List<String>> level, String param) {
        Path path = Path.createPath(param);

        List<String> msgs = level.get(path);
        if (msgs != null) {
            return msgs;
        }
        else {
            return Collections.emptyList();
        }
    }

    // -- Field

    @Override
    public Field getField() {
        return getField(null);
    }

    @Override
    public Messages getMessages() {
        return getMessages(null);
    }

    FormNode formPath(Path path) {
        Node unnamed = Node.createPrefix(null);
        FormNode root = new FormNode(unnamed);
        root.getDefinedChild(path);

        Binder binder = binderFactory.getBinder(parameterRef, root.hasChildren());
        if (binder != Binder.NULL_BINDER) {
            binder.readComposite(root);
            root.setBinder(binder);
        }

        return root;
    }

    private String normalParam(String param) {
        // TODO: e.g. user[username] to user.username
        return param;
    }

    String getDefaultConversionInfo(String param) {
        Element element = getConversionElement(param);
        if (element != null) {
            String message = element.tcp.c.getSimpleName();
            return message;
        }
        else {
            return null;
        }
    }

    List<String> getDefaultValidationInfos(String param) {
        ElementDescriptor element = getValidationElement(param);
        if (element != null) {
            List<String> messages = new ArrayList<String>();
            for (ConstraintDescriptor<?> constraint : element.getConstraintDescriptors()) {
                Annotation annotation = constraint.getAnnotation();
                String message = annotation.annotationType().getSimpleName();
                messages.add(message);
            }
            return messages;
        }
        else {
            return Collections.emptyList();
        }
    }

    private Element getConversionElement(String param) {
        Path path = Path.createPath(param);
        FormNode root = formPath(path);
        FormNode node = root.getDefinedChild(path);
        return node.getElement();
    }

    private ElementDescriptor getValidationElement(String param) {
        Validator validator = validatorFactory.getValidator();
        Class<?> clazz = parameter.tcp.c;

        if (Utils.isNullOrEmpty(param)) {
            BeanDescriptor bean = validator.getConstraintsForClass(clazz);
            return bean;
        }
        else {
            String normalParam = normalParam(param);
            if (normalParam != null) {
                BeanDescriptor bean = validator.getConstraintsForClass(clazz);
                PropertyDescriptor property = bean.getConstraintsForProperty(normalParam);
                return property;
            }
            else {
                return null;
            }
        }
    }

    // -- Conversion

    String toStringValue(Element element, Object value) {
        return Utils.getFirst(toStringList(element, value));
    }

    List<String> toStringList(Element element, Object value) {

        @SuppressWarnings("unchecked")
        MultivaluedConverter<Object> extractor = (MultivaluedConverter<Object>) converterProvider.getMultivalued(
                element.tcp.t,
                element.tcp.c,
                element.annotations);

        if (extractor != null) {
            return extractor.toStringList(value);
        }
        else {
            return Collections.emptyList();
        }
    }

    Conversion fromStringList(Element element, List<String> values) {

        MultivaluedConverter<?> extractor = converterProvider.getMultivalued(
                element.tcp.t,
                element.tcp.c,
                element.annotations,
                element.encoded,
                element.defaultValue);

        if (extractor != null) {
            try {
                Object value = extractor.fromStringList(values);
                return Conversion.fromValue(value);
            }
            catch (ConverterException ex) {
                return Conversion.fromError(ex);
            }
        }
        else {
            return null;
        }
    }

}
