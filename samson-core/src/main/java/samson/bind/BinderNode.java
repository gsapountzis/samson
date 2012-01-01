package samson.bind;

import java.util.Collection;
import java.util.List;

import samson.convert.ConverterException;

public interface BinderNode<T extends BinderNode<?>> {

    // -- Binder

    Binder getBinder();

    void setBinder(Binder binder);

    // -- Node

    String getName();

    boolean hasChild(String name);

    T getChild(String name);

    boolean hasChildren();

    Collection<T> getChildren();

    // -- Leaf

    List<String> getStringValues();

    void setStringValues(List<String> values);

    ConverterException getConversionFailure();

    void setConversionFailure(ConverterException conversionError);

}
