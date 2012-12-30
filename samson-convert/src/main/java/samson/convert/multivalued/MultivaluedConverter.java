package samson.convert.multivalued;

import java.util.List;

public interface MultivaluedConverter<T> {

    T fromStringList(List<String> stringList);

    List<String> toStringList(T object);

}
