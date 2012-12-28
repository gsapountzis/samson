package samson.jersey.bind;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.junit.Ignore;
import org.junit.Test;

import samson.form.SamsonForm;
import samson.jersey.test.util.WebappTestUtils;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.WebApplication;

public class BeanTest {

    public static class Bean {
        public Integer num;
    }

    private static String string(Bean bean) {
        StringBuilder sb = new StringBuilder();
        sb.append(bean.num);
        return sb.toString();
    }

    @Path("/bean")
    public static class BeanResource {

        // -- query parameter

        @Path("query")
        @GET
        public String query(@QueryParam("") SamsonForm<Bean> bean) {
            return string(bean.get());
        }

        @Path("queryBean")
        @GET
        public String queryBean(@QueryParam("bean") SamsonForm<Bean> bean) {
            return string(bean.get());
        }

        // -- form parameter

        @Path("form")
        @POST
        public String form(@FormParam("") SamsonForm<Bean> bean) {
            return string(bean.get());
        }

        @Path("formBean")
        @POST
        public String formBean(@FormParam("bean") SamsonForm<Bean> bean) {
            return string(bean.get());
        }

        // -- entity body

        @Path("body")
        @POST
        public String body(SamsonForm<Bean> bean) {
            return string(bean.get());
        }

    }

    @Test
    @Ignore
    public void testQuery() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(BeanResource.class);
        WebResource r = WebappTestUtils.resource(w);

        URI u = UriBuilder.fromPath("bean/query")
                .queryParam("num", "1")
                .build();

        assertEquals("1", r.uri(u).get(String.class));

        // error
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("bean/query")
                .queryParam("num", "error")
                .build();

        assertEquals("null", r.uri(u).get(String.class));
    }


    @Test
    public void testQueryBean() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(BeanResource.class);
        WebResource r = WebappTestUtils.resource(w);

        URI u = UriBuilder.fromPath("bean/queryBean")
                .queryParam("bean.num", "1")
                .build();

        assertEquals("1", r.uri(u).get(String.class));

        // error
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        u = UriBuilder.fromPath("bean/queryBean")
                .queryParam("bean.num", "error")
                .build();

        assertEquals("null", r.uri(u).get(String.class));
    }

    @Test
    @Ignore
    public void testForm() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(BeanResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("num", "1");

        assertEquals("1", r.path("bean/form").post(String.class, form));

        // error
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("num", "error");

        assertEquals("null", r.path("bean/form").post(String.class, form));
    }


    @Test
    public void testFormBean() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(BeanResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("bean.num", "1");

        assertEquals("1", r.path("bean/formBean").post(String.class, form));

        // error
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("bean.num", "error");

        assertEquals("null", r.path("bean/formBean").post(String.class, form));
    }

    @Test
    @Ignore
    public void testBodyBean() {
        // ok
        WebApplication w = WebappTestUtils.createWepapp(BeanResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("num", "1");

        assertEquals("1", r.path("bean/body").post(String.class, form));

        // error
        w = WebappTestUtils.createWepapp(BeanResource.class);
        r = WebappTestUtils.resource(w);

        form = new Form();
        form.add("num", "error");

        assertEquals("null", r.path("bean/body").post(String.class, form));
    }

}
