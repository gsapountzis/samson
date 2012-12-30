package samson.jersey.convert.multivalued;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import samson.convert.Converter;
import samson.convert.ConverterProvider;
import samson.convert.multivalued.MultivaluedConverter;
import samson.convert.multivalued.MultivaluedConverterProvider;
import samson.jersey.convert.multivalued.JerseyMultivaluedConverters.CollectionMultivaluedConverter;
import samson.jersey.convert.multivalued.JerseyMultivaluedConverters.PrimitiveMultivaluedConverter;
import samson.jersey.convert.multivalued.JerseyMultivaluedConverters.SingularMultivaluedConverter;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.reflection.ReflectionHelper.TypeClassPair;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;

public class JerseyMultivaluedConverterProvider implements MultivaluedConverterProvider {

    private final ConverterProvider converterProvider;

    private MultivaluedParameterExtractorProvider mpep;

    public JerseyMultivaluedConverterProvider(ConverterProvider converterProvider) {
        this.converterProvider = converterProvider;
    }

    // -- Setter injection for Jersey's custom DI

    public void setExtractorProvider(MultivaluedParameterExtractorProvider mpep) {
        this.mpep = mpep;
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
                null, JerseyMultivaluedConverters.PARAMETER_NAME,
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
                Converter<Object> converter = converterProvider.get(tcp.c, tcp.t, annotations);
                if (converter == null) {
                    return null;
                }
                return new CollectionMultivaluedConverter<T>(extractor, converter);
            }
            else {
                Converter<T> converter = converterProvider.get(rawType, type, annotations);
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
