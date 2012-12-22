package samson.bind;

import java.util.Collection;
import java.util.List;

import samson.convert.ConverterException;
import samson.metadata.ElementRef;

public interface BinderNode<T extends BinderNode<?>> {

    // -- Node

    String getName();

    boolean hasChild(String name);

    T getChild(String name);

    boolean hasChildren();

    Collection<T> getChildren();

    // -- Reference

    ElementRef getRef();

    void setRef(ElementRef ref);

    // -- Value

    List<String> getStringValues();

    void setStringValues(List<String> values);

    ConverterException getConversionFailure();

    void setConversionFailure(ConverterException conversionError);

}
