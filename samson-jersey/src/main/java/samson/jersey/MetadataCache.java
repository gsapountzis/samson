package samson.jersey;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.Element;
import samson.metadata.MethodMetadata;
import samson.metadata.MethodMetadataCache;
import samson.metadata.MethodParameter;
import samson.metadata.TypeClassPair;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.reflection.ReflectionHelper;

class MetadataCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataCache.class);

    private final MethodMetadataCache methodCache;

    MetadataCache() {
        this.methodCache = new MethodMetadataCache();
    }

    Element getArgumentElement(AccessibleObject ao, Parameter parameter) {
        Annotation[] annotations = parameter.getAnnotations();
        Annotation annotation = parameter.getAnnotation();

        Type type = parameter.getParameterType();

        ReflectionHelper.TypeClassPair tcp = ReflectionHelper.getTypeArgumentAndClass(type);
        if (tcp == null) {
            throw new ContainerException("Parameterized type without type arguement");
        }
        TypeClassPair samsonTcp = new TypeClassPair(tcp.t, tcp.c);

        Element.JaxrsAnnotations jaxrsAnnotations = new Element.JaxrsAnnotations(
                parameter.getSourceName(),
                parameter.isEncoded(),
                parameter.getDefaultValue());

        Element element = new Element(annotations, samsonTcp, jaxrsAnnotations);

        if (ao instanceof Constructor) {
            // constructor injection
            return element;
        }
        else if (ao instanceof Field) {
            // field injection
            return element;
        }
        else if (ao instanceof Method) {
            Method method = (Method) ao;

            final boolean resource = true;
            if (resource) {
                // resource method, sub-resource method, sub-resource locator
                MethodMetadata metadata = methodCache.get(method);
                boolean unique = isUniqueParameters(metadata);
                if (unique == false) {
                    throw new IllegalArgumentException("Parameter names must be unique for each method.");
                }

                MethodParameter methodParameter = metadata.findParameter(annotation);
                if (methodParameter != null) {
                    // new instance with jaxrs annotations set
                    return element;
                }
                else {
                    // setter injection
                    return element;
                }
            }
            else {
                // setter injection
                return element;
            }
        }
        else {
            throw new IllegalStateException("Unknown type of accessible object");
        }
    }

    private final ConcurrentMap<Method, Boolean> isUniqueParametersCache = new ConcurrentHashMap<Method, Boolean>();

    private boolean isUniqueParameters(MethodMetadata metadata) {
        Method method = metadata.getMethod();
        Boolean memo = isUniqueParametersCache.get(method);
        if (memo == null) {
            Boolean result = computeUniqueParameters(metadata);
            memo = isUniqueParametersCache.putIfAbsent(method, result);
            if (memo == null) {
                memo = result;
            }
        }
        return memo;
    }

    private boolean computeUniqueParameters(MethodMetadata metadata) {
        Method method = metadata.getMethod();
        Set<String> names = new HashSet<String>();

        for (MethodParameter parameter : metadata.getParameters()) {
            for (Annotation annotation : parameter.annotations) {
                String name = null;
                if (FormParam.class == annotation.annotationType()) {
                    FormParam param = (FormParam) annotation;
                    name = param.value();
                }
                else if (QueryParam.class == annotation.annotationType()) {
                    QueryParam param = (QueryParam) annotation;
                    name = param.value();
                }

                if (name != null) {
                    LOGGER.trace("{}: found parameter with name '{}'", method, name);
                    if (names.contains(name)) {
                        LOGGER.error("{}: duplicate parameter name '{}'", method, name);
                        return false;
                    }
                    else {
                        names.add(name);
                    }
                }
            }
        }

        return true;
    }
}
