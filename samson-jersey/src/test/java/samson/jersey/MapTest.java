package samson.jersey;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import samson.JForm;
import samson.jersey.util.WebappTestUtils;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.WebApplication;

public class MapTest {

    private static String string(Map<String, Integer> map) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("zero").append("=").append(map.get("zero")).append(", ");
        sb.append("one").append("=").append(map.get("one")).append("}");
        return sb.toString();
    }

    @Path("/")
    public static class MapResource {

        // -- query parameter

        @Path("query")
        @GET
        public String query(@QueryParam("map") JForm<Map<String, Integer>> mapForm) {
            Map<String, Integer> map = mapForm.get();
            return string(map);
        }

        // -- form parameter

        @Path("form")
        @POST
        public String form(@FormParam("map") JForm<Map<String, Integer>> mapForm) {
            Map<String, Integer> map = mapForm.get();
            return string(map);
        }

    }

    @Test
    public void testQueryList() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(MapResource.class);
        WebResource r = WebappTestUtils.resource(w);

        URI u = UriBuilder.fromPath("query")
                .queryParam("map[zero]", "0")
                .queryParam("map[one]", "1")
                .build();

        assertEquals("{zero=0, one=1}", r.uri(u).get(String.class));

        // missing
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("query")
                .queryParam("map[one]", "1")
                .build();

        assertEquals("{zero=null, one=1}", r.uri(u).get(String.class));

        // error
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("query")
                .queryParam("map[zero]", "error")
                .queryParam("map[one]", "1")
                .build();

        assertEquals("{zero=null, one=1}", r.uri(u).get(String.class));
    }

    @Test
    public void testFormList() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(MapResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("map[zero]", "0");
        form.add("map[one]", "1");

        assertEquals("{zero=0, one=1}", r.path("form").post(String.class, form));

        // missing
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("map[one]", "1");

        assertEquals("{zero=null, one=1}", r.path("form").post(String.class, form));

        // error
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("map[zero]", "error");
        form.add("map[one]", "1");

        assertEquals("{zero=null, one=1}", r.path("form").post(String.class, form));
    }

}
