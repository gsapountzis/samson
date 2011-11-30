package samson.example.scalate.resources;

import java.net.URI;
import java.net.URISyntaxException;

public class Paths {

    public static class products {
        public static URI view(Long id) {
            try {
                return new URI(String.format("/products/%d", id));
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

}
