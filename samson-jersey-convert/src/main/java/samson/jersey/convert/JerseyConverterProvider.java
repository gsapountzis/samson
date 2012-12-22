package samson.jersey.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import samson.convert.Converter;
import samson.convert.ConverterProvider;
import samson.convert.MultivaluedConverter;
import samson.jersey.convert.JerseyConverters.DateConverter;
import samson.jersey.convert.JerseyConverters.JerseyConverter;
import samson.jersey.convert.JerseyConverters.StringConverter;
import samson.jersey.convert.JerseyMultivaluedConverters.CollectionMultivaluedConverter;
import samson.jersey.convert.JerseyMultivaluedConverters.PrimitiveMultivaluedConverter;
import samson.jersey.convert.JerseyMultivaluedConverters.SingularMultivaluedConverter;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.reflection.ReflectionHelper.TypeClassPair;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderWorkers;

public class JerseyConverterProvider implements ConverterProvider {

    public static final String PARAMETER_NAME = "name";

    private final JerseyConverterPredicate stringTypePredicate;
    private StringReaderWorkers srw;
    private MultivaluedParameterExtractorProvider mpep;

    public JerseyConverterProvider() {
        this.stringTypePredicate = new JerseyConverterPredicate();
    }

    // -- Setter injection for Jersey's custom DI

    public void setStringReaderProvider(StringReaderWorkers srw) {
        this.srw = srw;
        this.stringTypePredicate.setStringReaderProvider(srw);
    }

    public void setExtractorProvider(MultivaluedParameterExtractorProvider mpep) {
        this.mpep = mpep;
        this.stringTypePredicate.setExtractorProvider(mpep);
    }

    // -- Conversion Predicate

    @Override
    public boolean isConvertible(Class<?> rawType, Type type) {
        return stringTypePredicate.isStringType(type, rawType);
    }

    // -- Converter

    @SuppressWarnings("unchecked")
    @Override
    public <T> Converter<T> get(Class<T> rawType, Type type, Annotation annotations[]) {

        if (rawType == String.class) {
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

    // -- Multivalued Converter

    @Override
    public <T> MultivaluedConverter<T> getMultivalued(Class<T> rawType, Type type, Annotation annotations[]) {
        return getMultivalued(rawType, type, annotations, false, null);
    }

    @Override
    public <T> MultivaluedConverter<T> getMultivalued(Class<T> rawType, Type type, Annotation[] annotations, boolean encoded, String defaultValue) {

        Parameter parameter = new Parameter(
                annotations, null,
                null, PARAMETER_NAME,
                type, rawType,
                encoded, defaultValue);

        MultivaluedParameterExtractor extractor = mpep.get(parameter);
        if (extractor != null) {

            if (rawType.isPrimitive()) {
                return new PrimitiveMultivaluedConverter<T>(extractor);
            }
            else if (rawType == List.class ||
                     rawType == Set.class ||
                     rawType == SortedSet.class)
            {
                TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(type);
                if (tcp == null) {
                    tcp = new TypeClassPair(String.class, String.class);
                }

                @SuppressWarnings("unchecked")
                Converter<Object> converter = this.get(tcp.c, tcp.t, annotations);
                if (converter == null) {
                    return null;
                }
                return new CollectionMultivaluedConverter<T>(extractor, converter);
            }
            else {
                Converter<T> converter = this.get(rawType, type, annotations);
                if (converter == null) {
                    return null;
                }
                return new SingularMultivaluedConverter<T>(extractor, converter);
            }
        }
        else {
            return null;
        }
    }

}
