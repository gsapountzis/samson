package samson.form;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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

    // -- Messages

    final Map<Path, List<String>> infos = new HashMap<Path, List<String>>();
    final Map<Path, List<String>> errors = new HashMap<Path, List<String>>();

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

    // -- Form

    @Override
    public T get() {
        return parameterValue;
    }

    @Override
    public List<String> getInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getErrors() {
        throw new UnsupportedOperationException();
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

    // -- Field

    @Override
    public Field getField() {
        return getField(null);
    }

    abstract Field getField(final String param);

    @Override
    public Messages getMessages() {
        return getMessages(null);
    }

    abstract Messages getMessages(final String param);

    @Override
    public void info(String msg) {
        info(null, msg);
    }

    public void info(String path, String msg) {
        message(infos, path, msg);
    }

    @Override
    public void error(String msg) {
        error(null, msg);
    }

    public void error(String path, String msg) {
        message(errors, path, msg);
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

    // -- Default Messages

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
        List<String> messages = new ArrayList<String>();
        ElementDescriptorTuple element = getValidationElement(param);
        if (element != null) {
            getDefaultValidationInfos(messages, element.type);
            getDefaultValidationInfos(messages, element.decl);
        }
        return messages;
    }

    private void getDefaultValidationInfos(List<String> messages, ElementDescriptor element) {
        if (element != null) {
            for (ConstraintDescriptor<?> constraint : element.getConstraintDescriptors()) {
                Annotation annotation = constraint.getAnnotation();
                String message = annotation.annotationType().getSimpleName();
                messages.add(message);
            }
        }
    }

    private static class ElementDescriptorTuple {
        final ElementDescriptor type;
        final ElementDescriptor decl;

        ElementDescriptorTuple(ElementDescriptor type, ElementDescriptor decl) {
            this.type = type;
            this.decl = decl;
        }
    }

    private Element getConversionElement(String param) {
        Path path = Path.createPath(param);
        FormNode root = formPath(path);
        FormNode node = root.getDefinedChild(path);
        return node.getElement();
    }

    private ElementDescriptorTuple getValidationElement(String param) {
        Validator validator = validatorFactory.getValidator();

        Path path = Path.createPath(param);
        FormNode root = formPath(path);
        FormNode node = root.getDefinedChild(path);
        Element element = node.getElement();

        if (element != null) {
            Class<?> clazz = element.tcp.c;
            BeanDescriptor bean = validator.getConstraintsForClass(clazz);

            int length = path.size();
            if (length == 0) {
                return new ElementDescriptorTuple(bean, /* method parameter */ null);
            }
            else {
                FormNode parent = root;
                Iterator<Node> iter = path.iterator();
                for (int i = 0; i < length - 1; i++) {
                    parent = parent.getChild(iter.next());
                }
                FormNode child = parent.getChild(iter.next());
                if (child != node) {
                    throw new IllegalStateException();
                }

                Element parentElement = parent.getElement();
                Class<?> parentClass = parentElement.tcp.c;
                BeanDescriptor parentBean = validator.getConstraintsForClass(parentClass);
                PropertyDescriptor property = parentBean.getConstraintsForProperty(child.getName());

                return new ElementDescriptorTuple(bean, property);
            }
        }
        else {
            return null;
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
