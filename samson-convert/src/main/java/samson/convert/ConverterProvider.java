package samson.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ConverterProvider {

    boolean isConvertible(Type type, Class<?> rawType);

    <T> Converter<T> get(Type type, Class<T> rawType, Annotation annotations[]);

    <T> MultivaluedConverter<T> getMultivalued(Type type, Class<T> rawType, Annotation annotations[]);

    <T> MultivaluedConverter<T> getMultivalued(Type type, Class<T> rawType, Annotation annotations[], boolean encoded, String defaultValue);

}
