package samson.jersey.convert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import samson.convert.Converter;
import samson.convert.ConverterException;

import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.spi.StringReader;

class JerseyConverters {

    public static class StringConverter implements Converter<String> {

        @Override
        public String fromString(String string) {
            return string;
        }

        @Override
        public String toString(String object) {
            return object;
        }
    };

    public static class JerseyConverter<T> implements Converter<T> {

        final StringReader<T> delegate;

        public JerseyConverter(StringReader<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public T fromString(String string) {
            try {
                return delegate.fromString(string);
            }
            catch(ExtractorContainerException ex) {
                // handle empty as null, return default value
                if (Utils.isNullOrEmpty(string)) {
                    return delegate.fromString(null);
                }
                else {
                    throw new ConverterException(ex.getMessage(), ex.getCause());
                }
            }
        }

        @Override
        public String toString(T object) {
            if (object != null) {
                return object.toString();
            }
            else {
                return null;
            }
        }

    }

    public static class DateConverter extends JerseyConverter<Date> {

        // not thread-safe, cannot be static could be ThreadLocal
        private final DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        public DateConverter(StringReader<Date> delegate) {
            super(delegate);
        }

        @Override
        public String toString(Date object) {
            if (object != null) {
                return dateFormat.format(object);
            }
            else {
                return null;
            }
        }

    }
}
