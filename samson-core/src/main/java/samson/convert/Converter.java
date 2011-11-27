package samson.convert;

public interface Converter<T> {

    T fromString(String string);

    String toString(T object);
}
