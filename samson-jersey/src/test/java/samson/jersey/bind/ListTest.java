package samson.jersey.bind;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import samson.form.SamsonForm;
import samson.jersey.test.util.WebappTestUtils;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.WebApplication;

public class ListTest {

    @Path("/")
    public static class ListResource {

        // -- query parameter

        @Path("query")
        @GET
        public String query(@QueryParam("list") SamsonForm<List<Integer>> listForm) {
            List<Integer> list = listForm.get();
            return list.toString();
        }

        // -- form parameter

        @Path("form")
        @POST
        public String form(@FormParam("list") SamsonForm<List<Integer>> listForm) {
            List<Integer> list = listForm.get();
            return list.toString();
        }

    }

    @Test
    public void testQueryList() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(ListResource.class);
        WebResource r = WebappTestUtils.resource(w);

        URI u = UriBuilder.fromPath("query")
                .queryParam("list[0]", "0")
                .queryParam("list[1]", "1")
                .build();

        assertEquals("[0, 1]", r.uri(u).get(String.class));

        // missing
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("query")
                .queryParam("list[1]", "1")
                .build();

        assertEquals("[null, 1]", r.uri(u).get(String.class));

        // error
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("query")
                .queryParam("list[0]", "error")
                .queryParam("list[1]", "1")
                .build();

        assertEquals("[null, 1]", r.uri(u).get(String.class));
    }

    @Test
    public void testFormList() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(ListResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("list[0]", "0");
        form.add("list[1]", "1");

        assertEquals("[0, 1]", r.path("form").post(String.class, form));

        // missing
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("list[1]", "1");

        assertEquals("[null, 1]", r.path("form").post(String.class, form));

        // error
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("list[0]", "error");
        form.add("list[1]", "1");

        assertEquals("[null, 1]", r.path("form").post(String.class, form));
    }

}
