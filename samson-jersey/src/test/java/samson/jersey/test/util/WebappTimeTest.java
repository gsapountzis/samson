package samson.jersey.test.util;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import samson.JForm;
import samson.jersey.test.example.register.User;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.WebApplication;

public class WebappTimeTest {

    private static long[] t = new long[8];

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        t[0] = System.currentTimeMillis();

        t[1] = System.currentTimeMillis();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Path("/")
    public static class TestResource {

        private void print(User user) {
            System.out.println(user.getEmail());
            System.out.println(user.getAddress().getStreet());
            System.out.println(user.getAddress().getZipcode());
        }

        @GET
        public String get(@QueryParam("") JForm<User> userForm) {
            User user = userForm.get();
            print(user);
            return "";
        }

        @POST
        public String post(@FormParam("") JForm<User> userForm) {
            User user = userForm.get();
            print(user);
            return "";
        }

        @Path("body")
        @POST
        public String body(JForm<User> userForm) {
            User user = userForm.get();
            print(user);
            return "";
        }
    }

    @Test
    public void testGet() {
        t[2] = System.currentTimeMillis();

        WebApplication w = WebappTestUtils.createWepapp(TestResource.class);
        WebResource r = WebappTestUtils.resource(w);

        URI u = UriBuilder.fromPath("")
                .queryParam("email", "foo@bar.com")
                .queryParam("address.zipcode", "62400")
                .build();

        assertEquals("", r.uri(u).get(String.class));

        t[3] = System.currentTimeMillis();

        System.out.println("init time: " + (t[1] - t[0]));
        System.out.println("dead time: " + (t[2] - t[1]));
        System.out.println("test time: " + (t[3] - t[2]));
    }

    @Test
    public void testPost() {
        t[4] = System.currentTimeMillis();

        WebApplication w = WebappTestUtils.createWepapp(TestResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("email", "foo@bar.com");
        form.add("address.zipcode", "62400");

        assertEquals("", r.post(String.class, form));

        t[5] = System.currentTimeMillis();

        System.out.println("dead time: " + (t[4] - t[3]));
        System.out.println("test time: " + (t[5] - t[4]));

        assertEquals("", r.path("body").post(String.class, form));

        t[6] = System.currentTimeMillis();

        System.out.println("test time: " + (t[6] - t[5]));
    }

}
