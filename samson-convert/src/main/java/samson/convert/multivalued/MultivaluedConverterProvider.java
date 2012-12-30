package samson.convert.multivalued;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface MultivaluedConverterProvider {

    <T> MultivaluedConverter<T> getMultivalued(Class<T> rawType, Type type, Annotation annotations[]);

    <T> MultivaluedConverter<T> getMultivalued(Class<T> rawType, Type type, Annotation annotations[], boolean encoded, String defaultValue);

}
