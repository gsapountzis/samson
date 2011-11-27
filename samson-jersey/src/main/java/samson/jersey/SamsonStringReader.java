package samson.jersey;

import samson.convert.Converter;
import samson.convert.ConverterException;

import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.spi.StringReader;

public class SamsonStringReader<T> implements Converter<T> {

    private final StringReader<T> delegate;

    public SamsonStringReader(StringReader<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T fromString(String string) {
        try {
            return delegate.fromString(string);
        }
        catch(ExtractorContainerException ex) {
            throw new ConverterException(ex.getCause());
        }
    }

    @Override
    public String toString(T object) {
        if (object != null) {
            return object.toString();
        }
        else {
            return null;
        }
    }

}
