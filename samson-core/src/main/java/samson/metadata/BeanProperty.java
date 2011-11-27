package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

import samson.jersey.core.reflection.ReflectionHelper;

public abstract class BeanProperty extends Element {

    private BeanProperty(Annotation[] annotations, TypeClassPair tcp, String name) {
        super(annotations, tcp, name);
    }

    public abstract Object get(Object bean);

    public abstract void set(Object bean, Object value);

    public static BeanProperty fromPublicField(Class<?> beanClass, Annotation[] annotations, String name, Field field) {

        TypeClassPair tcp = createTcp(
                beanClass,
                field.getDeclaringClass(),
                field.getType(),
                field.getGenericType());

        final int modifiers = tcp.c.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalArgumentException("Non-public field");
        }

        return new FieldBeanProperty(annotations, tcp, name, field);
    }

    public static BeanProperty fromProperty(Class<?> beanClass, Annotation[] annotations, String name, Method getter, Method setter) {

        TypeClassPair tcp = createTcp(
                beanClass,
                setter.getDeclaringClass(),
                setter.getParameterTypes()[0],
                setter.getGenericParameterTypes()[0]);

        return new MethodBeanProperty(annotations, tcp, name, getter, setter);
    }

    private static class FieldBeanProperty extends BeanProperty {

        private final Field field;

        FieldBeanProperty(Annotation[] annotations, TypeClassPair tcp, String name, Field field) {
            super(annotations, tcp, name);
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

        MethodBeanProperty(Annotation[] annotations, TypeClassPair tcp, String name, Method getter, Method setter) {
            super(annotations, tcp, name);
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

}