package samson.jersey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import samson.convert.Converter;
import samson.convert.ConverterProvider;

import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderWorkers;

public class SamsonStringReaderWorkers implements ConverterProvider {

    private final StringReaderWorkers delegate;

    public SamsonStringReaderWorkers(StringReaderWorkers delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> Converter<T> get(Class<T> type, Type genericType, Annotation[] annotations) {

        StringReader<T> stringReader = delegate.getStringReader(type, genericType, annotations);
        if (stringReader != null) {
            return new SamsonStringReader<T>(stringReader);
        }
        else {
            return null;
        }
    }

}
