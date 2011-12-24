package samson.bind;

import java.util.Collection;

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

}
