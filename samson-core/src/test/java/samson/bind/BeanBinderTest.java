package samson.bind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import samson.JFormProvider;
import samson.test.util.UnitTestUtils;

import com.sun.jersey.api.representation.Form;

public class BeanBinderTest {

    private static JFormProvider jForm;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        jForm = UnitTestUtils.createJFormProvider();
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    public static class Bean {

        private int a;

        private NestedBean nestedBean;

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public NestedBean getNestedBean() {
            return nestedBean;
        }

        public void setNestedBean(NestedBean nestedBean) {
            this.nestedBean = nestedBean;
        }

    }

    public static class NestedBean {

        private int b;

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }
    }

    @Test
    public void testNestedBean() {
        Form form = new Form();
        form.add("bean.a", "3");
        form.add("bean.nestedBean.b", "4");

        Bean bean = jForm.bind(Bean.class).params("bean", form).get();

        assertEquals(3, bean.getA());
        assertEquals(4, bean.getNestedBean().getB());
    }

    @Test
    public void testNullNestedBean() {
        Form form = new Form();
        form.add("bean.a", "1");

        Bean bean = jForm.bind(Bean.class).params("bean", form).get();

        assertEquals(1, bean.getA());
        assertNull(bean.getNestedBean());
    }

    public static class FormBean {
        public Bean beanOne;
        public Bean beanTwo;
    }

    @Test
    public void testSameNestedBean() {
        Form form = new Form();
        form.add("bean.beanOne.a", "1");
        form.add("bean.beanOne.nestedBean[b]", "2");
        form.add("bean.beanTwo[a]", "3");
        form.add("bean.beanTwo[nestedBean].b", "4");

        FormBean bean = jForm.bind(FormBean.class).params("bean", form).get();

        assertEquals(1, bean.beanOne.getA());
        assertEquals(2, bean.beanOne.getNestedBean().getB());
        assertEquals(3, bean.beanTwo.getA());
        assertEquals(4, bean.beanTwo.getNestedBean().getB());
    }

    public static class RecursiveBean {
        public int a;
        public RecursiveBean recursiveBean;
    }

    public static class RecursiveBeanRight {
        public int a;
        public RecursiveBeanLeft left;
    }

    public static class RecursiveBeanLeft {
        public int b;
        public RecursiveBeanRight right;
    }

    @Test
    public void testRecursive() {
        Form form = new Form();
        form.add("bean.a", "1");
        form.add("bean.recursiveBean.a", "2");

        RecursiveBean bean = jForm.bind(RecursiveBean.class).params("bean", form).get();

        assertEquals(1, bean.a);
        assertEquals(2, bean.recursiveBean.a);
    }

    @Test
    public void testRecursiveIndirect() {
        Form form = new Form();
        form.add("bean.a", "1");
        form.add("bean.left.b", "2");
        form.add("bean.left.right.a", "3");

        RecursiveBeanRight bean = jForm.bind(RecursiveBeanRight.class).params("bean", form).get();

        assertEquals(1, bean.a);
        assertEquals(2, bean.left.b);
        assertEquals(3, bean.left.right.a);
    }

}
