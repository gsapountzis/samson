package samson.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface MultivaluedTypePredicate {

    boolean apply(Class<?> type, Type genericType, Annotation annotations[]);

}
