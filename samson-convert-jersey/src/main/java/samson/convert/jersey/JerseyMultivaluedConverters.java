package samson.convert.jersey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import samson.convert.Converter;
import samson.convert.ConverterException;
import samson.convert.MultivaluedConverter;

import com.sun.jersey.api.representation.Form;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;

class JerseyMultivaluedConverters {

    public static final String PARAMETER_NAME = "name";

    private static Form form(List<String> stringList) {
        Form form = new Form();
        form.put(PARAMETER_NAME, stringList);
        return form;
    }

    public static abstract class JerseyMultivaluedConverter<T> implements MultivaluedConverter<T> {

        final MultivaluedParameterExtractor delegate;

        public JerseyMultivaluedConverter(MultivaluedParameterExtractor delegate) {
            this.delegate = delegate;
        }

        @SuppressWarnings("unchecked")
        @Override
        public T fromStringList(List<String> stringList) {
            try {
                return (T) delegate.extract(form(stringList));
            }
            catch(ExtractorContainerException ex) {
                // handle empty as null, return default value
                List<String> nullStringList = new ArrayList<String>(stringList.size());
                for (String s : stringList) {
                    nullStringList.add(Utils.isNullOrEmpty(s) ? null : s);
                }
                try {
                    return (T) delegate.extract(form(nullStringList));
                }
                catch(ExtractorContainerException nullEx) {
                    // do nothing
                }

                throw new ConverterException(ex.getMessage(), ex.getCause());
            }
        }

    }

    public static class PrimitiveMultivaluedConverter<T> extends JerseyMultivaluedConverter<T> {

        public PrimitiveMultivaluedConverter(MultivaluedParameterExtractor delegate) {
            super(delegate);
        }

        private static String toString(Object object) {
            return (object != null) ? object.toString() : null;
        }

        @Override
        public List<String> toStringList(T object) {
            List<String> stringList = new ArrayList<String>();
            stringList.add(toString(object));
            return stringList;
        }

    }

    public static class SingularMultivaluedConverter<T> extends JerseyMultivaluedConverter<T> {

        private final Converter<T> converter;

        public SingularMultivaluedConverter(MultivaluedParameterExtractor delegate, Converter<T> converter) {
            super(delegate);
            this.converter = converter;
        }

        @Override
        public List<String> toStringList(T object) {
            List<String> stringList = new ArrayList<String>();
            stringList.add(converter.toString(object));
            return stringList;
        }

    }

    public static class CollectionMultivaluedConverter<T> extends JerseyMultivaluedConverter<T> {

        private final Converter<Object> converter;

        public CollectionMultivaluedConverter(MultivaluedParameterExtractor delegate, Converter<Object> converter) {
            super(delegate);
            this.converter = converter;
        }

        @Override
        public List<String> toStringList(T object) {
            Collection<?> collection = (Collection<?>) object;
            List<String> stringList = new ArrayList<String>();
            for (Object item : collection) {
                stringList.add(converter.toString(item));
            }
            return stringList;
        }

    }

}
