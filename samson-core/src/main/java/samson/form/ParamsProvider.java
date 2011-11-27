package samson.form;

import javax.ws.rs.core.MultivaluedMap;

public interface ParamsProvider {

    MultivaluedMap<String, String> get();

}
