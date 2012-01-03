package samson;

import samson.metadata.Element;

public interface JFormBuilder {

    <T> JForm<T> wrap(Class<T> type);

    <T> JForm<T> wrap(Class<T> type, T instance);

    <T> JForm<T> bind(Class<T> type);

    <T> JForm<T> bind(Class<T> type, T instance);

    JForm<?> bind(Element element);

}
