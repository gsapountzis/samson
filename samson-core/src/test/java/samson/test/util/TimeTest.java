package samson.test.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import samson.JFormProvider;
import samson.test.example.register.User;

import com.sun.jersey.api.representation.Form;

public class TimeTest {

    private static long[] t = new long[8];

    private static JFormProvider jForm;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        t[0] = System.currentTimeMillis();

        jForm = UnitTestUtils.createJFormProvider();

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

    public void doTestBind() {
        Form form = new Form();
        form.add("user.email", "foo@bar.com");
        form.add("user.address.zipcode", "62400");

        User user = jForm.params("user", form).bind(User.class).get();

        assertEquals("foo@bar.com", user.getEmail());
        assertNotNull(user.getAddress());
        assertNull(user.getAddress().getStreet());
        assertEquals("62400", user.getAddress().getZipcode());
    }

    @Test
    public void testBind1() {
        t[2] = System.currentTimeMillis();

        // this one pays the cost of introspecting User.class
        doTestBind();

        t[3] = System.currentTimeMillis();

        System.out.println("init time: " + (t[1] - t[0]));
        System.out.println("dead time: " + (t[2] - t[1]));
        System.out.println("test time: " + (t[3] - t[2]));
    }

    @Test
    public void testBind2() {
        t[4] = System.currentTimeMillis();

        doTestBind();

        t[5] = System.currentTimeMillis();

        System.out.println("dead time: " + (t[4] - t[3]));
        System.out.println("test time: " + (t[5] - t[4]));
    }

    @Test
    public void testBind3() {
        // eclipse incorrectly reports time for last test, adds the time to start the next test
    }

}
