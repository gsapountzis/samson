package samson;

import javax.ws.rs.core.MultivaluedMap;

public interface JFormBuilder<T> {

    JForm<T> params(MultivaluedMap<String, String> params);

    JForm<T> params(String path, MultivaluedMap<String, String> params);

    JForm<T> form();

    JForm<T> form(String path);

    JForm<T> query();

    JForm<T> query(String path);

}
