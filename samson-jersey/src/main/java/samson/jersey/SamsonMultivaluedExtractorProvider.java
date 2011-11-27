package samson.jersey;

import samson.convert.MultivaluedExtractor;
import samson.convert.MultivaluedExtractorProvider;
import samson.metadata.Element;

import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractorProvider;

public class SamsonMultivaluedExtractorProvider implements MultivaluedExtractorProvider {

    private final MultivaluedParameterExtractorProvider delegate;

    public SamsonMultivaluedExtractorProvider(MultivaluedParameterExtractorProvider delegate) {
        this.delegate = delegate;
    }

    @Override
    public MultivaluedExtractor get(Element element) {

        Parameter parameter = new Parameter(
                element.annotations,
                null,
                null,
                element.name,
                element.tcp.t,
                element.tcp.c,
                element.encoded,
                element.defaultValue);

        MultivaluedParameterExtractor extractor = delegate.get(parameter);
        if (extractor != null) {
            return new SamsonMultivaluedExtractor(element.name, extractor);
        }
        else {
            return null;
        }
    }

}
