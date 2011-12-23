package samson.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface ConverterPredicate {

    boolean apply(Type type, Class<?> rawType, Annotation annotations[]);

}
