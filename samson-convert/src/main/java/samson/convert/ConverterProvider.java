package samson.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ConverterProvider {

    <T> Converter<T> get(Type type, Class<T> rawType, Annotation annotations[]);

}
