package samson.jersey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.URI;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import samson.JForm;
import samson.jersey.util.WebappTestUtils;

import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.WebApplication;

public class PrimitiveTest {

    @Path("/")
    public static class PrimitiveResource {

        // -- query parameter

        @Path("query")
        @GET
        public String query(@QueryParam("param") @DefaultValue("-1") Integer param) {
            return "" + param;
        }

        @Path("queryForm")
        @GET
        public String queryForm(@QueryParam("param") @DefaultValue("-1") JForm<Integer> param) {
            Integer i = param.get();
            return "" + i;
        }

        // -- form parameter

        @Path("form")
        @POST
        public String form(@FormParam("param") @DefaultValue("-1") Integer param) {
            return "" + param;
        }

        @Path("formForm")
        @POST
        public String formForm(@FormParam("param") @DefaultValue("-1") JForm<Integer> param) {
            Integer i = param.get();
            return "" + i;
        }

    }

    @Test
    public void testQuery() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        WebResource r = WebappTestUtils.resource(w);

        URI u = UriBuilder.fromPath("query")
                .queryParam("param", "1")
                .build();

        assertEquals("1", r.uri(u).get(String.class));

        // missing
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("query").build();
        assertEquals("-1", r.uri(u).get(String.class));

        // error
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("query")
                .queryParam("param", "error")
                .build();

        try {
            r.uri(u).get(String.class);
            fail();
        } catch (UniformInterfaceException ex) {
            // expected
        }
    }

    @Test
    public void testQueryForm() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        WebResource r = WebappTestUtils.resource(w);

        URI u = UriBuilder.fromPath("queryForm")
                .queryParam("param", "1")
                .build();

        assertEquals("1", r.uri(u).get(String.class));

        // missing
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("queryForm").build();
        assertEquals("-1", r.uri(u).get(String.class));

        // error
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("queryForm")
                .queryParam("param", "error")
                .build();

        assertEquals("null", r.uri(u).get(String.class));
    }

    @Test
    public void testForm() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("param", "1");

        assertEquals("1", r.path("form").post(String.class, form));

        // missing
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        assertEquals("-1", r.path("form").post(String.class, form));

        // error
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("param", "error");

        try {
            r.path("form").post(String.class, form);
            fail();
        } catch (UniformInterfaceException ex) {
            // expected
        }
    }

    @Test
    public void testFormForm() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("param", "1");

        assertEquals("1", r.path("formForm").post(String.class, form));

        // missing
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        assertEquals("-1", r.path("formForm").post(String.class, form));

        // error
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("param", "error");

        assertEquals("null", r.path("formForm").post(String.class, form));
    }

}
