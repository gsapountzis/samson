package samson.jersey.bind;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import samson.jersey.test.util.WebappTestUtils;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.WebApplication;

public class NullTest {

    private static final String STRING_NULL = "string/null";
    private static final String STRING_EMPTY = "";
    private static final String STRING_DEFAULT = "default";

    private static final String INT_NULL = "int/null";
    private static final String INT_ZERO = "0";
    private static final String INT_DEFAULT = "10";

    @Path("/")
    public static class PrimitiveResource {

        @Path("string")
        @GET
        public String stringMethod(@QueryParam("param") String param) {
            if (param != null) {
                return param;
            }
            else {
                return STRING_NULL;
            }
        }

        @Path("stringWithDefault")
        @GET
        public String stringWithDefault(@QueryParam("param") @DefaultValue(STRING_DEFAULT) String param) {
            if (param != null) {
                return param;
            }
            else {
                return STRING_NULL;
            }
        }

        @Path("int")
        @GET
        public String intMethod(@QueryParam("param") int param) {
            return Integer.valueOf(param).toString();
        }

        @Path("intWithDefault")
        @GET
        public String intWithDefault(@QueryParam("param") @DefaultValue(INT_DEFAULT) int param) {
            return Integer.valueOf(param).toString();
        }

        @Path("intWrapped")
        @GET
        public String intWrapperMethod(@QueryParam("param") Integer param) {
            if (param != null) {
                return param.toString();
            }
            else {
                return INT_NULL;
            }
        }

        @Path("intWrappedWithDefault")
        @GET
        public String intWrapperWithDefault(@QueryParam("param") @DefaultValue(INT_DEFAULT) Integer param) {
            if (param != null) {
                return param.toString();
            }
            else {
                return INT_NULL;
            }
        }

    }

    @Test
    public void testString() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("string").build();
        s = r.uri(u).get(String.class);
        assertEquals(STRING_NULL, s);

        // empty
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("string").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(STRING_EMPTY, s);
    }

    @Test
    public void testStringWithDefault() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("stringWithDefault").build();
        s = r.uri(u).get(String.class);
        assertEquals(STRING_DEFAULT, s);

        // empty
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("stringWithDefault").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(STRING_EMPTY, s);
    }

    @Test
    public void testInt() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("int").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_ZERO, s);

        // empty
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("int").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_ZERO, s);
    }

    @Test
    public void testIntWithDefault() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("intWithDefault").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_DEFAULT, s);

        // empty
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("intWithDefault").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_DEFAULT, s);
    }

    @Test
    public void testIntWrapped() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("intWrapped").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_NULL, s);

        // empty
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("intWrapped").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_NULL, s);
    }

    @Test
    public void testIntWrappedWithDefault() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("intWrappedWithDefault").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_DEFAULT, s);

        // empty
        w = WebappTestUtils.createWepapp(PrimitiveResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("intWrappedWithDefault").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_DEFAULT, s);
    }

    private static final String LIST_NULL = "list/null";
    private static final String LIST_EMPTY = "list/empty";
    private static final String LIST_STRING_EMPTY = "[]";
    private static final String LIST_STRING_EMPTY_EMPTY = "[, ]";
    private static final String LIST_INT_NULL = "[null]";
    private static final String LIST_INT_NULL_NULL = "[null, null]";

    @Path("/list")
    public static class ListResource {

        @Path("string")
        @GET
        public String stringMethod(@QueryParam("param") List<String> list) {
            if (list != null) {
                if (list.isEmpty()) {
                    return LIST_EMPTY;
                }
                else {
                    return list.toString();
                }
            }
            else {
                return LIST_NULL;
            }
        }

        @Path("int")
        @GET
        public String intMethod(@QueryParam("param") List<Integer> list) {
            if (list != null) {
                if (list.isEmpty()) {
                    return LIST_EMPTY;
                }
                else {
                    return list.toString();
                }
            }
            else {
                return LIST_NULL;
            }
        }

    }

    @Test
    public void testStringList() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/string").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_EMPTY, s);

        // empty
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/string").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_STRING_EMPTY, s);

        // empty x 2
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/string").queryParam("param", "", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_STRING_EMPTY_EMPTY, s);
    }

    @Test
    public void testIntList() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/int").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_EMPTY, s);

        // empty
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/int").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_INT_NULL, s);

        // empty x 2
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/int").queryParam("param", "", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_INT_NULL_NULL, s);
    }

}
