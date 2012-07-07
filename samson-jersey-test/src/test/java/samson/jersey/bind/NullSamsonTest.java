package samson.jersey.bind;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.junit.Test;

import samson.form.SamsonForm;
import samson.jersey.test.util.WebappTestUtils;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.container.WebApplication;

public class NullSamsonTest {

    private static final String STRING_NULL = "string/null";
    private static final String STRING_EMPTY = "";
    private static final String STRING_DEFAULT = "default";

    private static final String INT_NULL = "int/null";
    private static final String INT_DEFAULT = "10";

    @Path("/")
    public static class PrimitiveResource {

        @Path("string")
        @GET
        public String stringMethod(@QueryParam("param") SamsonForm<String> samsonParam) {
            String param = samsonParam.get();
            if (param != null) {
                return param;
            }
            else {
                return STRING_NULL;
            }
        }

        @Path("stringWithDefault")
        @GET
        public String stringWithDefault(@QueryParam("param") @DefaultValue(STRING_DEFAULT) SamsonForm<String> samsonParam) {
            String param = samsonParam.get();
            if (param != null) {
                return param;
            }
            else {
                return STRING_NULL;
            }
        }

        @Path("intWrapped")
        @GET
        public String intWrapperMethod(@QueryParam("param") SamsonForm<Integer> samsonParam) {
            Integer param = samsonParam.get();
            if (param != null) {
                return param.toString();
            }
            else {
                return INT_NULL;
            }
        }

        @Path("intWrappedWithDefault")
        @GET
        public String intWrapperWithDefault(@QueryParam("param") @DefaultValue(INT_DEFAULT) SamsonForm<Integer> samsonParam) {
            Integer param = samsonParam.get();
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
        public String stringMethod(@QueryParam("param") SamsonForm<List<String>> samsonList) {
            List<String> list = samsonList.get();
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
        public String intMethod(@QueryParam("param") SamsonForm<List<Integer>> samsonList) {
            List<Integer> list = samsonList.get();
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

        // empty[0]
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/string").queryParam("param[0]", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_STRING_EMPTY, s);

        // empty[0..1]
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/string").queryParam("param[0]", "").queryParam("param[1]", "").build();
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

        // empty[0]
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/int").queryParam("param[0]", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_INT_NULL, s);

        // empty[0..1]
        w = WebappTestUtils.createWepapp(ListResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("list/int").queryParam("param[0]", "").queryParam("param[1]", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(LIST_INT_NULL_NULL, s);
    }

    private static final String MAP_NULL = "map/null";
    private static final String MAP_EMPTY = "map/empty";
    private static final String MAP_STRING_EMPTY = "{zero=, one=string/null}";
    private static final String MAP_STRING_EMPTY_EMPTY = "{zero=, one=}";
    private static final String MAP_INT_NULL = "{zero=int/null, one=int/null}";
    private static final String MAP_INT_NULL_NULL = "{zero=int/null, one=int/null}";

    private static String mapString(String s) {
        return (s != null) ? s : STRING_NULL;
    }

    private static String mapInt(Integer i) {
        return (i != null) ? i.toString() : INT_NULL;
    }

    @Path("/map")
    public static class MapResource {

        @Path("string")
        @GET
        public String stringMethod(@QueryParam("param") SamsonForm<Map<String, String>> samsonMap) {
            Map<String, String> map = samsonMap.get();
            if (map != null) {
                if (map.isEmpty()) {
                    return MAP_EMPTY;
                }
                else {
                    StringBuilder sb = new StringBuilder()
                        .append("{")
                        .append("zero=")
                        .append(mapString(map.get("zero")))
                        .append(", ")
                        .append("one=")
                        .append(mapString(map.get("one")))
                        .append("}");

                    return sb.toString();
                }
            }
            else {
                return MAP_NULL;
            }
        }

        @Path("int")
        @GET
        public String intMethod(@QueryParam("param") SamsonForm<Map<String, Integer>> samsonMap) {
            Map<String, Integer> map = samsonMap.get();
            if (map != null) {
                if (map.isEmpty()) {
                    return MAP_EMPTY;
                }
                else {
                    StringBuilder sb = new StringBuilder()
                        .append("{")
                        .append("zero=")
                        .append(mapInt(map.get("zero")))
                        .append(", ")
                        .append("one=")
                        .append(mapInt(map.get("one")))
                        .append("}");

                    return sb.toString();
                }
            }
            else {
                return MAP_NULL;
            }
        }

    }

    @Test
    public void testStringMap() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("map/string").build();
        s = r.uri(u).get(String.class);
        assertEquals(MAP_NULL, s);

        // empty
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("map/string").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(MAP_NULL, s);

        // empty[zero]
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("map/string").queryParam("param[zero]", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(MAP_STRING_EMPTY, s);

        // empty[zero, one]
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("map/string").queryParam("param[zero]", "").queryParam("param[one]", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(MAP_STRING_EMPTY_EMPTY, s);
    }

    @Test
    public void testIntMap() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("map/int").build();
        s = r.uri(u).get(String.class);
        assertEquals(MAP_NULL, s);

        // empty
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("map/int").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(MAP_NULL, s);

        // empty[zero]
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("map/int").queryParam("param[zero]", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(MAP_INT_NULL, s);

        // empty[zero, one]
        w = WebappTestUtils.createWepapp(MapResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("map/int").queryParam("param[zero]", "").queryParam("param[one]", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(MAP_INT_NULL_NULL, s);
    }

    private static final String BEAN_NULL = "bean/null";

    public static class StringBean {
        public String value;
    }

    public static class IntBean {
        public Integer value;
    }

    @Path("/bean")
    public static class BeanResource {

        @Path("string")
        @GET
        public String stringMethod(@QueryParam("param") SamsonForm<StringBean> samsonBean) {
            StringBean bean = samsonBean.get();
            if (bean != null) {
                String value = bean.value;
                if (value != null) {
                    return value;
                }
                else {
                    return STRING_NULL;
                }
            }
            else {
                return BEAN_NULL;
            }
        }

        @Path("int")
        @GET
        public String intMethod(@QueryParam("param") SamsonForm<IntBean> samsonBean) {
            IntBean bean = samsonBean.get();
            if (bean != null) {
                Integer value = bean.value;
                if (value != null) {
                    return value.toString();
                }
                else {
                    return INT_NULL;
                }
            }
            else {
                return BEAN_NULL;
            }
        }

    }

    @Test
    public void testStringBean() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("bean/string").build();
        s = r.uri(u).get(String.class);
        assertEquals(BEAN_NULL, s);

        // empty bean
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("bean/string").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(BEAN_NULL, s);

        // empty bean property
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("bean/string").queryParam("param.value", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(STRING_EMPTY, s);
    }

    @Test
    public void testIntBean() {
        WebApplication w;
        WebResource r;
        URI u;
        String s;

        // null
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("bean/int").build();
        s = r.uri(u).get(String.class);
        assertEquals(BEAN_NULL, s);

        // empty bean
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("bean/int").queryParam("param", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(BEAN_NULL, s);

        // empty bean property
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("bean/int").queryParam("param.value", "").build();
        s = r.uri(u).get(String.class);
        assertEquals(INT_NULL, s);
    }

}
