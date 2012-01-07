package samson;

public interface JFormBuilder {

    <T> JForm<T> wrap(Class<T> type);

    <T> JForm<T> wrap(Class<T> type, T instance);

    <T> JForm<T> bind(Class<T> type);

    <T> JForm<T> bind(Class<T> type, T instance);

}
