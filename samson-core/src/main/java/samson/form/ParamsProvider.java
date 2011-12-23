package samson.form;

import java.util.List;
import java.util.Map;

public interface ParamsProvider {

    Map<String, List<String>> get();

}
