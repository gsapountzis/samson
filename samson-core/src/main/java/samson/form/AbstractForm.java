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

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.ConstraintDescriptor;
import javax.validation.metadata.ElementDescriptor;
import javax.validation.metadata.PropertyDescriptor;

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
                Conversion binding = getField(param).getConversion();
                if (binding == null) {
                    return null;
                }

                return binding.getElement().tcp.c.getSimpleName();
            }

            @SuppressWarnings("unused")
            private List<String> getDefaultValidationInfos() {
                ElementDescriptor element = getValidationElement();
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

            private ElementDescriptor getValidationElement() {
                Validator validator = validatorFactory.getValidator();

                // XXX must translate parameter name to javax.validation format: e.g user[username] vs. user.username
                // XXX must check for root bean

                Class<?> clazz = parameter.tcp.c;
                BeanDescriptor bean = validator.getConstraintsForClass(clazz);
                PropertyDescriptor property = bean.getConstraintsForProperty(param);
                return property;
            }
        };
    }

    // -- Conversion: toString()

    /**
     * Ad-hoc formatter.
     * <p>
     * XXX Parse/Format framework.
     * <p>
     * Should be replaced with a parse/format framework provided by the JAX-RS
     * implementation. Right now Jersey only supports the parsing part, while
     * RESTEasy seems to have support for both.
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

    // cannot be static, non thread-safe (could use ThreadLocal)
    private final DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

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
