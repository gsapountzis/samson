package samson.jersey.convert;

import static samson.jersey.convert.JerseyMultivaluedConverters.PARAMETER_NAME;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import samson.convert.Converter;
import samson.convert.ConverterProvider;
import samson.convert.MultivaluedConverter;
import samson.convert.MultivaluedConverterProvider;
import samson.jersey.convert.JerseyMultivaluedConverters.CollectionMultivaluedConverter;
import samson.jersey.convert.JerseyMultivaluedConverters.PrimitiveMultivaluedConverter;
import samson.jersey.convert.JerseyMultivaluedConverters.SingularMultivaluedConverter;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.reflection.ReflectionHelper.TypeClassPair;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;

public class JerseyMultivaluedConverterProvider implements MultivaluedConverterProvider {

    private final ConverterProvider converterProvider;
    private MultivaluedParameterExtractorProvider delegate;

    public JerseyMultivaluedConverterProvider(ConverterProvider converterProvider) {
        this.converterProvider = converterProvider;
    }

    public void setExtractorProvider(MultivaluedParameterExtractorProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> MultivaluedConverter<T> get(Type type, Class<T> rawType, Annotation annotations[]) {
        return get(type, rawType, annotations, false, null);
    }

    @Override
    public <T> MultivaluedConverter<T> get(Type type, Class<T> rawType, Annotation[] annotations, boolean encoded, String defaultValue) {

        Parameter parameter = new Parameter(
                annotations, null,
                null, PARAMETER_NAME,
                type, rawType,
                encoded, defaultValue);

        MultivaluedParameterExtractor extractor = delegate.get(parameter);
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
                Converter<Object> converter = converterProvider.get(tcp.t, tcp.c, annotations);
                if (converter == null) {
                    return null;
                }
                return new CollectionMultivaluedConverter<T>(extractor, converter);
            }
            else {
                Converter<T> converter = converterProvider.get(type, rawType, annotations);
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
