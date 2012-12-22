package samson.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ConverterProvider {

    boolean isConvertible(Class<?> rawType, Type type);

    <T> Converter<T> get(Class<T> rawType, Type type, Annotation annotations[]);

    <T> MultivaluedConverter<T> getMultivalued(Class<T> rawType, Type type, Annotation annotations[]);

    <T> MultivaluedConverter<T> getMultivalued(Class<T> rawType, Type type, Annotation annotations[], boolean encoded, String defaultValue);

}
