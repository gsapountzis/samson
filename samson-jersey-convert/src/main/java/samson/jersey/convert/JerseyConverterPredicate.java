package samson.jersey.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderWorkers;

class JerseyConverterPredicate {

    private StringReaderWorkers stringReaderProvider;
    private MultivaluedParameterExtractorProvider extractorProvider;

    public void setStringReaderProvider(StringReaderWorkers stringReaderProvider) {
        this.stringReaderProvider = stringReaderProvider;
    }

    public void setExtractorProvider(MultivaluedParameterExtractorProvider extractorProvider) {
        this.extractorProvider = extractorProvider;
    }

    private final ConcurrentMap<Integer, Boolean> resultCache = new ConcurrentHashMap<Integer, Boolean>();

    private static int hashCode(Type type, Class<?> rawType, Annotation annotations[]) {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(annotations);
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((rawType == null) ? 0 : rawType.hashCode());
        return result;
    }

    public boolean isStringType(Type type, Class<?> rawType, Annotation annotations[]) {

        int hashCode = hashCode(type, rawType, annotations);

        // drop memo ?
        Boolean memo = resultCache.get(hashCode);
        if (memo == null) {
            Boolean result = false;

            if (isStringTypeByClass(rawType)) {
                result = true;
            }
            else if (isStringTypeByReader(type, rawType, annotations)) {
                result = true;
            }
            else if (isStringTypeByExtractor(type, rawType, annotations)) {
                result = true;
            }

            memo = resultCache.putIfAbsent(hashCode, result);

            // not needed since boolean is instance-controlled, but follows java memo pattern
            if (memo == null) {
                memo = result;
            }
        }
        return memo;
    }

    private boolean isStringTypeByClass(Class<?> clazz) {

        if (clazz == String.class ||
            clazz == Boolean.class ||
            clazz == Byte.class ||
            clazz == Short.class ||
            clazz == Integer.class ||
            clazz == Long.class ||
            clazz == Float.class ||
            clazz == Double.class ||
            clazz == BigInteger.class ||
            clazz == BigDecimal.class)
        {
            return true;
        }

        if (Enum.class.isAssignableFrom(clazz) ||
            Date.class.isAssignableFrom(clazz))
        {
            return true;
        }

        return false;
    }

    private boolean isStringTypeByReader(Type type, Class<?> rawType, Annotation annotations[]) {

        StringReader<?> stringReader = stringReaderProvider.getStringReader(rawType, type, annotations);
        if (stringReader != null) {
            return true;
        }

        return false;
    }

    private boolean isStringTypeByExtractor(Type type, Class<?> rawType, Annotation annotations[]) {
        if (annotations == null) {
            annotations = new Annotation[0];
        }

        Parameter parameter = new Parameter(annotations, null, null, null, type, rawType);

        MultivaluedParameterExtractor extractor = extractorProvider.get(parameter);
        if (extractor != null) {
            return true;
        }

        return false;
    }

}
