package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import samson.jersey.core.reflection.ReflectionHelper;

public abstract class BeanProperty extends Element {

    public final TypeClassPair beanTcp;
    public final String propertyName;

    private BeanProperty(Element element, TypeClassPair beanTcp, String propertyName) {
        super(element.annotations, element.tcp, propertyName);
        this.beanTcp = beanTcp;
        this.propertyName = propertyName;
    }

    public static BeanProperty fromPublicField(TypeClassPair beanTcp, String propertyName, Field field) {

        Annotation[] annotations = field.getAnnotations();

        TypeClassPair tcp = createTcp(
                beanTcp.c,
                field.getDeclaringClass(),
                field.getType(),
                field.getGenericType());

        final int modifiers = tcp.c.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Non-public field");
        }

        Element element = new Element(annotations, tcp, null);
        return new FieldBeanProperty(element, beanTcp, propertyName, field);
    }

    public static BeanProperty fromProperty(TypeClassPair beanTcp, String propertyName, Method getter, Method setter, Field field) {

        Annotation[] argAnnotations = setter.getParameterAnnotations()[0];

        List<Annotation> list = asList(argAnnotations);
        if (getter != null) {
            mergeAnnotations(list, getter);
        }
        if (field != null) {
            mergeAnnotations(list, field);
        }

        Annotation[] annotations = list.toArray(new Annotation[0]);

        TypeClassPair tcp = createTcp(
                beanTcp.c,
                setter.getDeclaringClass(),
                setter.getParameterTypes()[0],
                setter.getGenericParameterTypes()[0]);

        Element element = new Element(annotations, tcp, null);
        return new MethodBeanProperty(element, beanTcp, propertyName, getter, setter);
    }

    public ElementAccessor createAccessor(final Object bean) {
        if (bean == null) {
            return ElementAccessor.NULL_ACCESSOR;
        }

        final BeanProperty property = this;

        return new ElementAccessor() {

            @Override
            public void set(Object value) {
                property.set(bean, value);
            }

            @Override
            public Object get() {
                return property.get(bean);
            }
        };
    }

    abstract Object get(Object bean);

    abstract void set(Object bean, Object value);

    private static class FieldBeanProperty extends BeanProperty {

        private final Field field;

        FieldBeanProperty(Element element, TypeClassPair beanTcp, String propertyName, Field field) {
            super(element, beanTcp, propertyName);
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

        MethodBeanProperty(Element element, TypeClassPair beanTcp, String propertyName, Method getter, Method setter) {
            super(element, beanTcp, propertyName);
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

    private static TypeClassPair createTcp(
            Class<?> concreteClass,
            Class<?> declaringClass,
            Class<?> paramClass,
            Type paramType)
    {
        ReflectionHelper.ClassTypePair ct = ReflectionHelper.getGenericType(concreteClass, declaringClass, paramClass, paramType);
        TypeClassPair tcp = new TypeClassPair(ct.t, ct.c);
        return tcp;
    }

    private static void mergeAnnotations(List<Annotation> list, AccessibleObject ao) {
        for (Annotation a : ao.getAnnotations()) {
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

    private static <T> List<T> asList(T... ts) {
        List<T> l = new ArrayList<T>();
        for (T t : ts) l.add(t);
        return l;
    }

}