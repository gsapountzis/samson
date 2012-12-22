package samson.convert.jersey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;
import com.sun.jersey.spi.StringReader;
import com.sun.jersey.spi.StringReaderWorkers;

class JerseyConverterPredicate {

    private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];

    private StringReaderWorkers stringReaderProvider;
    private MultivaluedParameterExtractorProvider extractorProvider;

    public void setStringReaderProvider(StringReaderWorkers stringReaderProvider) {
        this.stringReaderProvider = stringReaderProvider;
    }

    public void setExtractorProvider(MultivaluedParameterExtractorProvider extractorProvider) {
        this.extractorProvider = extractorProvider;
    }

    private final ConcurrentMap<Integer, Boolean> resultCache = new ConcurrentHashMap<Integer, Boolean>();

    private static int hashCode(Type type, Class<?> rawType) {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((rawType == null) ? 0 : rawType.hashCode());
        return result;
    }

    public boolean isStringType(Type type, Class<?> rawType) {

        int hashCode = hashCode(type, rawType);

        // drop memo ?
        Boolean memo = resultCache.get(hashCode);
        if (memo == null) {
            Boolean result = false;

            if (Utils.isBaseType(rawType)) {
                result = true;
            }
            else if (isStringTypeByReader(type, rawType)) {
                result = true;
            }
            else if (isStringTypeByExtractor(type, rawType)) {
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

    private boolean isStringTypeByReader(Type type, Class<?> rawType) {

        StringReader<?> stringReader = stringReaderProvider.getStringReader(rawType, type, EMPTY_ANNOTATIONS);
        if (stringReader != null) {
            return true;
        }

        return false;
    }

    private boolean isStringTypeByExtractor(Type type, Class<?> rawType) {

        Parameter parameter = new Parameter(EMPTY_ANNOTATIONS, null, null, null, type, rawType);

        MultivaluedParameterExtractor extractor = extractorProvider.get(parameter);
        if (extractor != null) {
            return true;
        }

        return false;
    }

}
