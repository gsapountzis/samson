package samson;

import samson.metadata.Element;

public interface JFormProvider {

    <T> JForm<T> bind(Class<T> type);

    <T> JForm<T> bind(Class<T> type, T instance);

    <T> JForm<T> wrap(Class<T> type);

    <T> JForm<T> wrap(Class<T> type, T instance);

    JForm<?> bind(Element element);

    JForm<?> wrap(Element element);

}
