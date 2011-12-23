package samson;

import java.util.List;
import java.util.Map;

public interface JFormBuilder<T> {

    JForm<T> params(Map<String, List<String>> params);

    JForm<T> params(String path, Map<String, List<String>> params);

    JForm<T> form();

    JForm<T> form(String path);

    JForm<T> query();

    JForm<T> query(String path);

}
