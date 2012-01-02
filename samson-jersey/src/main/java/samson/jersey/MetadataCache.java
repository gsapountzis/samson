package samson.jersey;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import samson.metadata.Element;
import samson.metadata.MethodMetadata;
import samson.metadata.MethodMetadataCache;
import samson.metadata.MethodParameter;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.reflection.ReflectionHelper.TypeClassPair;

class MetadataCache {

    private final MethodMetadataCache methodCache;

    MetadataCache() {
        this.methodCache = new MethodMetadataCache();
    }

    Element getArgumentElement(AccessibleObject ao, Parameter parameter) {
        Type type = parameter.getParameterType();

        TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(type);
        if (tcp == null) {
            throw new ContainerException("Parameterized type without type arguement");
        }

        Element element = new Element(
                parameter.getAnnotations(),
                tcp.t, tcp.c,
                parameter.getSourceName(),
                parameter.isEncoded(),
                parameter.getDefaultValue());

        Annotation annotation = parameter.getAnnotation();

        if (ao instanceof Constructor) {
            // constructor injection
            throw new UnsupportedOperationException();
        }
        else if (ao instanceof Field) {
            // field injection
            throw new UnsupportedOperationException();
        }
        else if (ao instanceof Method) {
            Method method = (Method) ao;

            final boolean resource = true;
            if (resource) {
                // resource method, sub-resource method, sub-resource locator
                MethodMetadata metadata = methodCache.get(method);
                MethodParameter methodParameter = metadata.findParameter(annotation);
                if (methodParameter != null) {
                    return new MethodParameter(element, method, methodParameter.parameterIndex);
                }
                else {
                    // setter injection
                    throw new UnsupportedOperationException();
                }
            }
            else {
                // setter injection
                throw new UnsupportedOperationException();
            }
        }
        else {
            throw new IllegalStateException();
        }
    }

}
