package samson.jersey.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;

import samson.convert.Converter;
import samson.convert.ConverterProvider;
import samson.jersey.convert.JerseyConverters.DateConverter;
import samson.jersey.convert.JerseyConverters.JerseyConverter;
import samson.jersey.convert.JerseyConverters.StringConverter;

import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderWorkers;

public class JerseyConverterProvider implements ConverterProvider {

    private StringReaderWorkers delegate;

    public JerseyConverterProvider() {
    }

    public void setStringReaderProvider(StringReaderWorkers delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Converter<T> get(Type type, Class<T> rawType, Annotation annotations[]) {

        if (rawType == String.class) {
            return (Converter<T>) new StringConverter();
        }
        else if (rawType == Date.class) {
            StringReader<Date> stringReader = delegate.getStringReader(Date.class, type, annotations);
            return (Converter<T>) new DateConverter(stringReader);
        }
        else {
            StringReader<T> stringReader = delegate.getStringReader(rawType, type, annotations);
            if (stringReader != null) {
                return new JerseyConverter<T>(stringReader);
            }
            else {
                return null;
            }
        }
    }

}
