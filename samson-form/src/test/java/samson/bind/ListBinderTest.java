package samson.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import samson.form.FormProvider;
import samson.test.util.UnitTestUtils;

import com.sun.jersey.api.representation.Form;

public class ListBinderTest {

    private static FormProvider jForm;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        jForm = UnitTestUtils.createFormProvider();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    public static class ListFormBean {
        public List<String> list;
    }

    @Test
    public void testList() {
        Form form = new Form();
        form.add("bean.list[2]", "1");
        form.add("bean.list[3]", "2");
        form.add("bean.list[0]", "3");

        List<String> list = jForm.params("bean", form).bind(ListFormBean.class).get().list;

        assertEquals(Arrays.asList("3", null, "1", "2"), list);
    }

    public static class ItemBean {
        public int a;
    }

    public static class ListBeanFormBean {
        public List<ItemBean> list;
    }

    private List<Integer> map(List<ItemBean> list) {
        List<Integer> result = new ArrayList<Integer>();
        for (ItemBean item : list) {
            Integer i = (item != null) ? item.a : null;
            result.add(i);
        }
        return result;
    }

    @Test
    public void testListBean() {
        Form form = new Form();
        form.add("bean.list[0].a", "0");
        form.add("bean.list[2].",  "");
        form.add("bean.list[3].a", "2");
        form.add("bean.list[4]",   "");
        form.add("bean.list[5].foo.bar", "");

        List<ItemBean> list = jForm.params("bean", form).bind(ListBeanFormBean.class).get().list;

        assertEquals(Arrays.asList(0, null, null, 2, 0, 0), map(list));
    }

    public static class BeanListBean {
        public List<Integer> list;
    }

    public static class BeanListFormBean {
        public BeanListBean bean;
    }

    @Test
    public void testBeanList() {
        Form form = new Form();
        form.add("bean.bean.list[2]", "1");
        form.add("bean.bean.list[3]", "2");
        form.add("bean.bean.list[0]", "3");

        List<Integer> list = jForm.params("bean", form).bind(BeanListFormBean.class).get().bean.list;

        assertEquals(Arrays.asList(3, null, 1, 2), list);
    }

    public static class ListListFormBean {
        public List<List<Integer>> list;
    }

    @Test
    public void testListList() {
        Form form = new Form();
        form.add("bean.list[0][1]", "1");
        form.add("bean.list[2][0]", "2");
        form.add("bean.list[3][0]", "3");
        form.add("bean.list[3][1]", "4");

        List<List<Integer>> list = jForm.params("bean", form).bind(ListListFormBean.class).get().list;

        assertEquals(2, list.get(0).size());
        assertNull(list.get(1));
        assertEquals(1, list.get(2).size());
        assertEquals(2, list.get(3).size());

        assertEquals(Arrays.asList(null, 1), list.get(0));
        assertEquals(Arrays.asList(2), list.get(2));
        assertEquals(Arrays.asList(3, 4), list.get(3));
    }

    public static class ListListBeanFormBean {
        public List<List<ItemBean>> list;
    }

    @Test
    public void testListListBean() {
        Form form = new Form();
        form.add("bean.list[0][1].a", "1");
        form.add("bean.list[2][0].a", "2");
        form.add("bean.list[3][0].a", "3");
        form.add("bean.list[3][1].a", "4");

        List<List<ItemBean>> list = jForm.params("bean", form).bind(ListListBeanFormBean.class).get().list;

        assertEquals(2, list.get(0).size());
        assertNull(list.get(1));
        assertEquals(1, list.get(2).size());
        assertEquals(2, list.get(3).size());

        assertEquals(Arrays.asList(null, 1), map(list.get(0)));
        assertEquals(Arrays.asList(2), map(list.get(2)));
        assertEquals(Arrays.asList(3, 4), map(list.get(3)));
    }


    public static class SetFormBean {
        public Set<String> set;
    }

    private <T> Set<T> set(List<T> list) {
        return new HashSet<T>(list);
    }

    @Test
    public void testSet() {
        Form form = new Form();
        form.add("bean.set", "1");
        form.add("bean.set", "5");
        form.add("bean.set", "2");
        form.add("bean.set", "4");
        form.add("bean.set", "5");

        Set<String> set = jForm.params("bean", form).bind(SetFormBean.class).get().set;

        assertEquals(set(Arrays.asList("1", "2", "4", "5")), set);
    }

    public static class SetBeanFormBean {
        public Set<ItemBean> set;
    }

    @Test
    public void testSetBeanNotBound() {
        Form form = new Form();
        form.add("bean.set.a", "1");
        form.add("bean.set.a", "2");
        form.add("bean.set.a", "3");

        try {
            jForm.params("bean", form).bind(SetBeanFormBean.class).get();
            fail();
        }
        catch (RuntimeException e) {
            // expected, Set cannot contain compound values
        }
    }

    public static class StringItemBean {
        int a;

        public StringItemBean(String a) {
            this.a = Integer.parseInt(a);
        }
    }

    public static class SetStringBeanFormBean {
        public Set<StringItemBean> set;
    }

    private Set<Integer> map(Set<StringItemBean> set) {
        Set<Integer> result = new HashSet<Integer>();
        for (StringItemBean item : set) {
            Integer i = (item != null) ? item.a : null;
            result.add(i);
        }
        return result;
    }

    @Test
    public void testSetStringBean() {
        Form form = new Form();
        form.add("bean.set", "3");
        form.add("bean.set", "2");
        form.add("bean.set", "1");

        Set<StringItemBean> set = jForm.params("bean", form).bind(SetStringBeanFormBean.class).get().set;

        assertEquals(set(Arrays.asList(1, 2, 3)), map(set));
    }

}
