package samson.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ConverterProvider {

    <T> Converter<T> get(Class<T> rawType, Type type, Annotation annotations[]);

}
