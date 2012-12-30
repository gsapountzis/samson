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

    private StringReaderWorkers srw;

    public JerseyConverterProvider() {
    }

    // -- Setter injection for Jersey's custom DI

    public void setStringReaderProvider(StringReaderWorkers srw) {
        this.srw = srw;
    }

    // -- Converter

    @SuppressWarnings("unchecked")
    @Override
    public <T> Converter<T> get(Class<T> rawType, Type type, Annotation annotations[]) {

        if (rawType.isPrimitive()) {
            throw new UnsupportedOperationException("Primitive converters not implemented yet");
        }
        else if (rawType == String.class) {
            return (Converter<T>) new StringConverter();
        }
        else if (rawType == Date.class) {
            StringReader<Date> stringReader = srw.getStringReader(Date.class, type, annotations);
            return (Converter<T>) new DateConverter(stringReader);
        }
        else {
            StringReader<T> stringReader = srw.getStringReader(rawType, type, annotations);
            if (stringReader != null) {
                return new JerseyConverter<T>(stringReader);
            }
            else {
                return null;
            }
        }
    }

}
