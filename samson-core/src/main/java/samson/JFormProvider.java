package samson;

import java.util.List;
import java.util.Map;

public interface JFormProvider {

    JFormBuilder path();

    JFormBuilder path(String path);

    JFormBuilder params(Map<String, List<String>> params);

    JFormBuilder params(String path, Map<String, List<String>> params);

    JFormBuilder form();

    JFormBuilder form(String path);

    JFormBuilder query();

    JFormBuilder query(String path);

    <T> JForm<T> wrap(Class<T> type);

    <T> JForm<T> wrap(Class<T> type, T instance);

}
