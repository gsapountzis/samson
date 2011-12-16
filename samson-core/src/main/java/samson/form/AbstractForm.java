package samson.form;

import java.lang.annotation.Annotation;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.PropertyDescriptor;
import javax.ws.rs.core.MultivaluedMap;

import samson.JForm;
import samson.bind.BinderFactory;
import samson.convert.Conversion;
import samson.convert.ConverterException;
import samson.convert.MultivaluedExtractor;
import samson.convert.MultivaluedExtractorProvider;
import samson.form.Property.Path;
import samson.metadata.Element;
import samson.metadata.Element.Accessor;
import samson.metadata.ListTcp;

abstract class AbstractForm<T> implements JForm<T> {

    protected final Element parameter;
    protected String rootPath;
    protected T value;

    protected BinderFactory binderFactory;
    protected ValidatorFactory validatorFactory;
    protected MultivaluedExtractorProvider extractorProvider;

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

    public void setExtractorProvider(MultivaluedExtractorProvider extractorProvider) {
        this.extractorProvider = extractorProvider;
    }

    @Override
    public JForm<T> params(MultivaluedMap<String, String> params) {
        return apply(params);
    }

    @Override
    public JForm<T> params(String path, MultivaluedMap<String, String> params) {
        this.rootPath = path;
        return apply(params);
    }

    @Override
    public JForm<T> form() {
        return apply(getFormParams());
    }

    @Override
    public JForm<T> form(String path) {
        this.rootPath = path;
        return apply(getFormParams());
    }

    @Override
    public JForm<T> query() {
        return apply(getQueryParams());
    }

    @Override
    public JForm<T> query(String path) {
        this.rootPath = path;
        return apply(getQueryParams());
    }

    protected abstract JForm<T> apply(MultivaluedMap<String, String> params);

    protected abstract MultivaluedMap<String, String> getFormParams();

    protected abstract MultivaluedMap<String, String> getQueryParams();

    @Override
    public final T get() {
        return getValue();
    }

    // -- Fields

    @Override
    public Map<String, Field> getFields() {
        return fields;
    }

    private final Map<String, Field> fields = new Map<String, AbstractForm.Field>() {

        @Override
        public int size() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsKey(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Field get(final Object key) {

            return new Field() {

                private final String param = (String) key;

                @Override
                public String getName() {
                    return param;
                }

                @Override
                public Object getObjectValue() {
                    return form.getObjectValue(param);
                }

                @Override
                public String getValue() {
                    return form.getValue(param);
                }

                @Override
                public List<String> getValues() {
                    return form.getValues(param);
                }

                @Override
                public boolean isError() {
                    return form.isError(param);
                }

                @Override
                public Conversion getConversion() {
                    return form.getConversion(param);
                }

                @Override
                public Set<ConstraintViolation<?>> getViolations() {
                    return form.getViolations(param);
                }

                @Override
                public Messages getMessages() {
                    return form.getMessages(param);
                }
            };

        }

        @Override
        public Field put(String key, Field value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Field remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends String, ? extends Field> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<String> keySet() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Field> values() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<java.util.Map.Entry<String, Field>> entrySet() {
            throw new UnsupportedOperationException();
        }

    };

    // -- Messages

    final Map<Path, List<String>> infos = new HashMap<Path, List<String>>();
    final Map<Path, List<String>> errors = new HashMap<Path, List<String>>();

    @Override
    public void info(String path, String msg) {
        message(infos, path, msg);
    }

    @Override
    public void error(String path, String msg) {
        message(errors, path, msg);
    }

    // XXX check that messages work for null-or-empty path i.e. root object

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
    public Messages getMessages(final String param) {

        return new Messages() {

            @Override
            public String getConversionInfo() {
                return form.getConversionInfo(param);
            }

            @Override
            public String getConversionError() {
                return form.getConversionError(param);
            }

            @Override
            public List<String> getValidationInfos() {
                return form.getValidationInfos(param);
            }

            @Override
            public List<String> getValidationErrors() {
                return form.getValidationErrors(param);
            }

            @Override
            public List<String> getInfos() {
                return getMessages(infos, param);
            }

            @Override
            public List<String> getErrors() {
                return getMessages(errors, param);
            }
        };
    }

    abstract String getConversionError(String param);

    abstract List<String> getValidationErrors(String param);

    String getConversionInfo(String param) {
        // XXX default info message for demo
        return getDefaultConversionInfo(param);
    }

    private String getDefaultConversionInfo(String param) {
        Conversion binding = getConversion(param);
        if (binding == null) {
            return null;
        }

        return binding.getElement().tcp.c.getSimpleName();
    }

    List<String> getValidationInfos(String param) {
        return Collections.emptyList();
    }

    @SuppressWarnings("unused")
    private List<String> getDefaultValidationInfos(String param) {
        ElementDescriptor element = getValidationElement(param);
        if (element != null) {
            List<String> messages = new ArrayList<String>();
            for (ConstraintDescriptor<?> constraint : element.getConstraintDescriptors()) {
                Annotation annotation = constraint.getAnnotation();
                String name = annotation.annotationType().getSimpleName();
                messages.add(name);
            }
            return messages;
        }
        return Collections.emptyList();
    }

    private ElementDescriptor getValidationElement(String param) {
        Validator validator = validatorFactory.getValidator();

        // XXX must translate parameter name to javax.validation format: e.g user[username] vs. user.username
        // XXX must check for root bean

        Class<?> clazz = parameter.tcp.c;
        BeanDescriptor bean = validator.getConstraintsForClass(clazz);
        PropertyDescriptor property = bean.getConstraintsForProperty(param);
        return property;
    }

    // -- Conversion: toString()

    // cannot be static, non thread-safe (could use ThreadLocal)
    private final DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

    /**
     * Ad-hoc formatter.
     *
     * XXX Should be replaced with a parser/formatter framework provided by the
     * JAX-RS implementation. Right now Jersey only supports the parsing part,
     * while RESTEasy seems to have support for both.
     */
    private String format(Element element, Object object) {
        if (object != null) {
            Class<?> clazz = element.tcp.c;
            if (clazz == Date.class) {
                Date date = (Date) object;
                return dateFormat.format(date);
            }
            else {
                return object.toString();
            }
        }
        return null;
    }

    protected String toStringValue(Element element, Object object) {
        Class<?> clazz = element.tcp.c;
        if (Collection.class.isAssignableFrom(clazz)) {
            Element itemElement = getItemElement(element);
            Collection<?> collection = (Collection<?>) object;
            Object item = Utils.getFirst(collection);
            return format(itemElement, item);
        }
        else {
            return format(element, object);
        }
    }

    protected List<String> toStringList(Element element, Object object) {
        List<String> stringList = new ArrayList<String>();

        Class<?> clazz = element.tcp.c;
        if (Collection.class.isAssignableFrom(clazz)) {
            Element itemElement = getItemElement(element);
            Collection<?> collection = (Collection<?>) object;
            for (Object item : collection) {
                stringList.add(format(itemElement, item));
            }
        }
        else {
            stringList.add(format(element, object));
        }
        return stringList;
    }

    private static Element getItemElement(Element listElement) {
        Annotation[] annotations = listElement.annotations;
        ListTcp listTcp = new ListTcp(listElement.tcp);

        return listTcp.getItemElement(annotations, "0");
    }

    // -- Conversion: fromString()

    protected Conversion fromStringList(Element element, List<String> values) {

        MultivaluedExtractor extractor = extractorProvider.get(element);
        if (extractor == null) {
            return null;
        }

        try {
            Object object = extractValue(extractor, values);
            return Conversion.fromValue(element, object);
        }
        catch (ConverterException ex) {
            // XXX handle empty as null
            String param = Utils.getFirst(values);
            if (Utils.isEmpty(param)) {
                Object object = extractDefaultValue(extractor);
                return Conversion.fromValue(element, object);
            }
            else {
                return Conversion.fromError(element, ex.getCause());
            }
        }
    }

    private Object extractValue(MultivaluedExtractor extractor, List<String> values) {
        return extractor.fromStringList(values);
    }

    private Object extractDefaultValue(MultivaluedExtractor extractor) {
        List<String> values = Collections.emptyList();
        return extractor.fromStringList(values);
    }

}
