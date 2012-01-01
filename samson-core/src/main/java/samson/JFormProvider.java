package samson;

import samson.metadata.Element;

public interface JFormProvider {

    <T> JForm<T> wrap(Class<T> type);

    <T> JForm<T> wrap(Class<T> type, T instance);

    JForm<?> wrap(Element element);

    <T> JFormBuilder<T> bind(Class<T> type);

    <T> JFormBuilder<T> bind(Class<T> type, T instance);

    JFormBuilder<?> bind(Element element);

}
