package samson.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.MultivaluedMap;

import samson.JForm;
import samson.bind.BinderFactory;
import samson.convert.Conversion;
import samson.convert.ConverterException;
import samson.convert.MultivaluedExtractor;
import samson.convert.MultivaluedExtractorProvider;
import samson.metadata.Element;
import samson.metadata.Element.Accessor;

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
                public String getConversionMessage() {
                    return form.getConversionMessage(param);
                }

                @Override
                public Set<ConstraintViolation<?>> getViolations() {
                    return form.getViolations(param);
                }

                @Override
                public List<String> getValidationMessages() {
                    return form.getValidationMessages(param);
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

    // -- Conversion: toString()

    private String convertToString(Object object) {
        // XXX use Converter toString(object)
        if (object != null) {
            return object.toString();
        }
        return null;
    }

    protected String toStringValue(Class<?> clazz, Object object) {
        if (Collection.class.isAssignableFrom(clazz)) {
            return Utils.getFirst(toStringList(clazz, object));
        }
        else {
            return convertToString(object);
        }
    }

    protected List<String> toStringList(Class<?> clazz, Object object) {
        List<String> stringList = new ArrayList<String>();

        if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> collection = (Collection<?>) object;
            for (Object element : collection) {
                stringList.add(convertToString(element));
            }
        }
        else {
            stringList.add(convertToString(object));
        }
        return stringList;
    }

    // -- Conversion: fromString()

    protected Conversion fromStringList(Element element, List<String> values) {

        MultivaluedExtractor extractor = extractorProvider.get(element);
        if (extractor == null) {
            return null;
        }

        Class<?> clazz = element.tcp.c;
        try {
            Object object = extractValue(extractor, values);
            return Conversion.fromValue(clazz, object);
        }
        catch (ConverterException ex) {
            // XXX handle empty as null
            String param = Utils.getFirst(values);
            if (Utils.isEmpty(param)) {
                Object object = extractDefaultValue(extractor);
                return Conversion.fromValue(clazz, object);
            }
            else {
                return Conversion.fromError(clazz, ex.getCause());
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
