package samson.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface MultivaluedConverterProvider {

    <T> MultivaluedConverter<T> get(Type type, Class<T> rawType, Annotation annotations[]);

    <T> MultivaluedConverter<T> get(Type type, Class<T> rawType, Annotation annotations[], boolean encoded, String defaultValue);

}
