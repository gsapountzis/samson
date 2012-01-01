package samson.jersey;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import samson.metadata.Element;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.reflection.ReflectionHelper.TypeClassPair;

class Utils {

    static Element getArgumentElement(AccessibleObject ao, Parameter parameter) {
        Type type = parameter.getParameterType();

        TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(type);
        if (tcp == null) {
            throw new ContainerException("Parameterized type without type arguement");
        }

        if (ao instanceof Constructor) {
            // constructor injection
        }
        else if (ao instanceof Field) {
            // field injection
        }
        else if (ao instanceof Method) {
            // resource method, sub-resource method, sub-resource locator
            // setter injection
        }

        return new Element(
                parameter.getAnnotations(),
                tcp.t, tcp.c,
                parameter.getSourceName(),
                parameter.isEncoded(),
                parameter.getDefaultValue());
    }

}
