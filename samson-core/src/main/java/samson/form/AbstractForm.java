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

import samson.Conversion;
import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.convert.ConverterException;
import samson.convert.MultivaluedConverter;
import samson.convert.MultivaluedConverterProvider;
import samson.form.Property.Path;
import samson.metadata.Element;
import samson.metadata.Element.Accessor;
import samson.metadata.ElementRef;
import samson.metadata.TypeClassPair;

abstract class AbstractForm<T> implements JForm<T> {

    protected final Element parameter;
    protected T value;

    protected BinderFactory binderFactory;
    protected ValidatorFactory validatorFactory;
    protected MultivaluedConverterProvider extractorProvider;

    protected final AbstractForm<T> form = this;

    protected final Accessor valueAccessor = new Accessor() {

        @Override
        public Object get() {
            return form.value;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void set(Object value) {
            form.value = (T) value;
        }

    };

    public AbstractForm(Element parameter) {
        this.parameter = parameter;
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

    @Override
    public T get() {
        return value;
    }

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

    @Override
    public Field getField() {
        return getField(null);
    }

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

    private static void message(Map<Path, List<String>> level, String param, String msg) {
        Path path = Path.createPath(param);

        List<String> msgs = level.get(path);
        if (msgs == null) {
            msgs = new ArrayList<String>();
            level.put(path, msgs);
        }
        msgs.add(msg);
    }

    private static List<String> getMessages(Map<Path, List<String>> level, String param) {
        Path path = Path.createPath(param);

        List<String> msgs = level.get(path);
        if (msgs != null) {
            return msgs;
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public Messages getMessages() {
        return getMessages(null);
    }

    @Override
    public Messages getMessages(final String param) {

        return new Messages() {

            @Override
            public String getConversionInfo() {
                return getDefaultConversionInfo();
            }

            @Override
            public String getConversionError() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<String> getValidationInfos() {
                return Collections.emptyList();
            }

            @Override
            public List<String> getValidationErrors() {
                throw new UnsupportedOperationException();
            }

            @Override
            public List<String> getInfos() {
                return getMessages(infos, param);
            }

            @Override
            public List<String> getErrors() {
                return getMessages(errors, param);
            }

            private String getDefaultConversionInfo() {
                ElementRef ref = getElementRef(param);
                if (ref == ElementRef.NULL_REF) {
                    return null;
                }

                String message = ref.element.tcp.c.getSimpleName();
                return message;
            }

            @SuppressWarnings("unused")
            private List<String> getDefaultValidationInfos() {
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
        };
    }

    protected ElementRef getElementRef(String param) {
        ElementRef ref = new ElementRef(parameter, valueAccessor);

        Property.Path path = Property.Path.createPath(param);
        for (Property.Node node : path) {
            Binder binder = binderFactory.getBinder(ref, true);
            ref = binder.getElementRef(node.getName());
            if (ref == ElementRef.NULL_REF) {
                return ElementRef.NULL_REF;
            }
        }

        return ref;
    }

    protected Conversion conversionFromElement(ElementRef ref) {
        if (ref != ElementRef.NULL_REF) {
            return Conversion.fromValue(
                    ref.element.annotations,
                    ref.element.tcp.t,
                    ref.element.tcp.c,
                    ref.accessor.get());
        }
        else {
            return null;
        }
    }

    private ElementDescriptor getValidationElement(String param) {
        Class<?> clazz = parameter.tcp.c;

        // XXX must translate parameter name to javax.validation format: e.g user[username] vs. user.username
        // XXX must check for root bean

        Validator validator = validatorFactory.getValidator();
        BeanDescriptor bean = validator.getConstraintsForClass(clazz);
        PropertyDescriptor property = bean.getConstraintsForProperty(param);
        return property;
    }

    protected String toStringValue(Conversion conversion) {
        return Utils.getFirst(toStringList(conversion));
    }

    protected List<String> toStringList(Conversion conversion) {

        @SuppressWarnings("unchecked")
        MultivaluedConverter<Object> extractor = (MultivaluedConverter<Object>) extractorProvider.get(
                conversion.getType(),
                conversion.getRawType(),
                conversion.getAnnotations());

        if (extractor != null) {
            return extractor.toStringList(conversion.getValue());
        }
        else {
            return Collections.emptyList();
        }
    }

    protected Conversion fromStringList(Element element, List<String> values) {

        MultivaluedConverter<?> extractor = extractorProvider.get(
                element.tcp.t,
                element.tcp.c,
                element.annotations,
                element.encoded,
                element.defaultValue);

        if (extractor != null) {
            Annotation[] annotations = element.annotations;
            TypeClassPair tcp = element.tcp;
            try {
                Object value = extractor.fromStringList(values);
                return Conversion.fromValue(annotations, tcp.t, tcp.c, value);
            }
            catch (ConverterException ex) {
                Throwable cause = ex.getCause();
                return Conversion.fromError(annotations, tcp.t, tcp.c, cause);
            }
        }
        else {
            return null;
        }
    }

}
