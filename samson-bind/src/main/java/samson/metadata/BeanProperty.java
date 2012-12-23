package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class BeanProperty extends Element {

    public final TypeClassPair beanTcp;
    public final String propertyName;

    private BeanProperty(Annotation[] annotations, TypeClassPair tcp, TypeClassPair beanTcp, String propertyName) {
        super(annotations, tcp);
        this.beanTcp = beanTcp;
        this.propertyName = propertyName;
    }

    public static BeanProperty fromPublicField(TypeClassPair beanTcp, String propertyName, Field field) {

        Annotation[] annotations = field.getAnnotations();

        TypeClassPair tcp = ReflectionHelper.resolveGenericType(
                beanTcp.c,
                field.getDeclaringClass(),
                field.getType(),
                field.getGenericType());

        return new FieldBeanProperty(annotations, tcp, beanTcp, propertyName, field);
    }

    public static BeanProperty fromProperty(TypeClassPair beanTcp, String propertyName, Method getter, Method setter, Field field) {

        List<Annotation> list = new ArrayList<Annotation>();
        if (getter != null) {
            mergeAnnotations(list, getter.getAnnotations());
        }
        if (field != null) {
            mergeAnnotations(list, field.getAnnotations());
        }

        Annotation[] annotations = list.toArray(new Annotation[list.size()]);

        TypeClassPair tcp = ReflectionHelper.resolveGenericType(
                beanTcp.c,
                setter.getDeclaringClass(),
                setter.getParameterTypes()[0],
                setter.getGenericParameterTypes()[0]);

        return new MethodBeanProperty(annotations, tcp, beanTcp, propertyName, getter, setter);
    }

    abstract Object get(Object bean);

    abstract void set(Object bean, Object value);

    private static class FieldBeanProperty extends BeanProperty {

        private final Field field;

        FieldBeanProperty(Annotation[] annotations, TypeClassPair tcp, TypeClassPair beanTcp, String propertyName, Field field) {
            super(annotations, tcp, beanTcp, propertyName);
            this.field = field;
        }

        @Override
        public Object get(Object bean) {
            try {
                return field.get(bean);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void set(Object bean, Object value) {
            try {
                field.set(bean, value);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static class MethodBeanProperty extends BeanProperty {

        private final Method getter;
        private final Method setter;

        MethodBeanProperty(Annotation[] annotations, TypeClassPair tcp, TypeClassPair beanTcp, String propertyName, Method getter, Method setter) {
            super(annotations, tcp, beanTcp, propertyName);
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public Object get(Object bean) {
            try {
                return getter.invoke(bean);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public void set(Object bean, Object value) {
            try {
                setter.invoke(bean, value);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (InvocationTargetException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void mergeAnnotations(List<Annotation> list, Annotation[] annotations) {
        for (Annotation a : annotations) {
            if (!isAnnotationPresent(list, a))
                list.add(a);
        }
    }

    private static boolean isAnnotationPresent(List<Annotation> annotations, Annotation annotation) {
        for (Annotation a : annotations) {
            if (a.annotationType() == annotation.annotationType())
                return true;
        }
        return false;
    }

}