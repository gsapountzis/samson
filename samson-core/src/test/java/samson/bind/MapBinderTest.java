package samson.bind;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import samson.form.FormProvider;
import samson.test.util.UnitTestUtils;

import com.sun.jersey.api.representation.Form;

public class MapBinderTest {

    private static FormProvider jForm;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        jForm = UnitTestUtils.createFormProvider();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    public static class MapFormBean {
        public Map<String, String> map;
    }

    @Test
    public void testMap() {
        Form form = new Form();
        form.add("bean.map[zero]", "0");
        form.add("bean.map[two]", "2");
        form.add("bean.map[three]", "3");

        Map<String, String> map = jForm.params("bean", form).bind(MapFormBean.class).get().map;

        assertEquals(3, map.size());
        assertEquals("0", map.get("zero"));
        assertEquals("2", map.get("two"));
        assertEquals("3", map.get("three"));
    }

    public static class ItemBean {
        public int a;
    }

    public static class MapBeanFormBean {
        public Map<Integer, ItemBean> map;
    }

    @Test
    public void testMapBean() {
        Form form = new Form();
        form.add("bean.map[0].a", "0");
        form.add("bean.map[two].a", "2"); // key parsing error
        form.add("bean.map[3].a", "3");

        Map<Integer, ItemBean> map = jForm.params("bean", form).bind(MapBeanFormBean.class).get().map;

        assertEquals(2, map.size());
        assertEquals(0, map.get(0).a);
        assertEquals(3, map.get(3).a);
    }

}
