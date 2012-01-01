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

    public final Class<?> beanClass;
    public final String propertyName;

    private BeanProperty(Annotation[] annotations, TypeClassPair tcp, Class<?> beanClass, String name) {
        super(annotations, tcp, name);
        this.beanClass = beanClass;
        this.propertyName = name;
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

    public static BeanProperty fromPublicField(Class<?> beanClass, String name, Field field) {

        Annotation[] annotations = field.getAnnotations();

        TypeClassPair tcp = createTcp(
                beanClass,
                field.getDeclaringClass(),
                field.getType(),
                field.getGenericType());

        final int modifiers = tcp.c.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Non-public field");
        }

        return new FieldBeanProperty(annotations, tcp, beanClass, name, field);
    }

    public static BeanProperty fromProperty(Class<?> beanClass, String name, Method getter, Method setter, Field field) {

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
                beanClass,
                setter.getDeclaringClass(),
                setter.getParameterTypes()[0],
                setter.getGenericParameterTypes()[0]);

        return new MethodBeanProperty(annotations, tcp, beanClass, name, getter, setter);
    }

    private static class FieldBeanProperty extends BeanProperty {

        private final Field field;

        FieldBeanProperty(Annotation[] annotations, TypeClassPair tcp, Class<?> beanClass, String name, Field field) {
            super(annotations, tcp, beanClass, name);
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

        MethodBeanProperty(Annotation[] annotations, TypeClassPair tcp, Class<?> beanClass, String name, Method getter, Method setter) {
            super(annotations, tcp, beanClass, name);
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