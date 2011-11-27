package samson.jersey;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.junit.Test;

import samson.JForm;
import samson.example.register.User;
import samson.jersey.util.WebappTestUtils;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.representation.Form;
import com.sun.jersey.spi.container.WebApplication;

public class RegisterTest {

    public static class RegisterForm {
        public User user;
        public boolean accept;
    }

    private static String string(User user) {
        return "{" + user.getEmail() + ", " + user.getDob() + ", " + user.getGender() + "}";
    }

    @Path("/")
    public static class RegisterResource {

        @Path("param")
        @POST
        public String param(@FormParam("") JForm<User> userForm, @FormParam("accept") boolean accept) {
            User user = userForm.get();

            return "{" + string(user) + ", " + accept + "}";
        }

        @Path("bean")
        @POST
        public String bean(@FormParam("user") JForm<User> userForm, @FormParam("accept") boolean accept) {
            User user = userForm.get();

            return "{" + string(user) + ", " + accept + "}";
        }

        @Path("body")
        @POST
        public String body(JForm<RegisterForm> registerForm) {
            RegisterForm register = registerForm.get();

            return "{" + string(register.user) + ", " + register.accept + "}";
        }

    }

    @Test
    public void testBeanParam() {
        WebApplication w = WebappTestUtils.createWepapp(RegisterResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("email", "foo");
        form.add("accept", "true");

        assertEquals("{{foo, null, null}, true}", r.path("param").post(String.class, form));
    }

    @Test
    public void testBeanBean() {
        WebApplication w = WebappTestUtils.createWepapp(RegisterResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("user.email", "foo");
        form.add("accept", "true");

        assertEquals("{{foo, null, null}, true}", r.path("bean").post(String.class, form));
    }

    @Test
    public void testBeanBody() {
        WebApplication w = WebappTestUtils.createWepapp(RegisterResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("user.email", "foo");
        form.add("accept", "true");

        assertEquals("{{foo, null, null}, true}", r.path("body").post(String.class, form));
    }

    @Test
    public void testBeanDateField() {
        WebApplication w = WebappTestUtils.createWepapp(RegisterResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("user.email", "foo");
        form.add("user.dob", "16 Aug 2011");

        assertEquals("{{foo, Tue Aug 16 00:00:00 EEST 2011, null}, false}", r.path("bean").post(String.class, form));
    }

    @Test
    public void testBeanEnumField() {
        WebApplication w = WebappTestUtils.createWepapp(RegisterResource.class);
        WebResource r = WebappTestUtils.resource(w);

        Form form = new Form();
        form.add("user.email", "bar");
        form.add("user.gender", "MALE");

        assertEquals("{{bar, null, MALE}, false}", r.path("bean").post(String.class, form));
    }

}
