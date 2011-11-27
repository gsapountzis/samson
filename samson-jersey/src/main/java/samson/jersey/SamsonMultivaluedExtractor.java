package samson.jersey;

import java.util.List;

import samson.convert.ConverterException;
import samson.convert.MultivaluedExtractor;

import com.sun.jersey.api.representation.Form;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.server.impl.model.parameter.multivalued.MultivaluedParameterExtractor;

public class SamsonMultivaluedExtractor implements MultivaluedExtractor {

    private final String name;
    private final MultivaluedParameterExtractor delegate;

    public SamsonMultivaluedExtractor(String name, MultivaluedParameterExtractor delegate) {
        this.name = name;
        this.delegate = delegate;
    }

    @Override
    public Object fromStringList(List<String> stringList) {
        Form form = new Form();
        form.put(name, stringList);

        try {
            return delegate.extract(form);
        }
        catch(ExtractorContainerException ex) {
            throw new ConverterException(ex.getCause());
        }
    }

}
