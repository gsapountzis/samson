package samson.bind;

import java.util.Collection;
import java.util.List;

public interface ParamNode<T extends ParamNode<?>> {

    String getName();

    List<String> getStringValues();

    boolean hasChild(String name);

    T getChild(String name);

    boolean hasChildren();

    Collection<T> getChildren();

}
