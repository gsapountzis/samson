package samson.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import samson.jersey.core.reflection.AnnotatedMethod;
import samson.jersey.core.reflection.MethodList;
import samson.jersey.spi.inject.Errors;

class BeanIntrospector {

    static BeanMetadata createBeanMetadata(Class<?> beanClass) {

        Map<String, Tuple> properties = new HashMap<String, Tuple>();

        checkClass(beanClass);

        findFields(beanClass, properties);

        final MethodList methodList = new MethodList(beanClass);

        findSetters(methodList, properties);

        findGetters(methodList, properties);

        return new BeanMetadata(beanClass, map(beanClass, properties));
    }

    private static void checkClass(Class<?> clazz) {
        final int modifiers = clazz.getModifiers();

        if (!Modifier.isPublic(modifiers)) {
            Errors.nonPublicClass(clazz);
        }

        if (Modifier.isAbstract(modifiers)) {
            if (Modifier.isInterface(modifiers)) {
                Errors.interfaceClass(clazz);
            } else {
                Errors.abstractClass(clazz);
            }
        }

        if (clazz.getEnclosingClass() != null && !Modifier.isStatic(modifiers)) {
            Errors.innerClass(clazz);
        }

        if (Modifier.isPublic(modifiers) && !Modifier.isAbstract(modifiers)) {
            if (clazz.getConstructors().length == 0) {
                Errors.nonPublicConstructor(clazz);
            }
        }
    }

    private static class Tuple {
        Field field;
        Method getter;
        Method setter;
    }

    private static Tuple getTuple(Map<String, Tuple> properties, String name) {
        Tuple t = properties.get(name);
        if (t == null) {
            t = new Tuple();
            properties.put(name, t);
        }
        return t;
    }

    private static void findFields(Class<?> c, Map<String, Tuple> properties) {
        if (c.isInterface())
            return;

        while (c != Object.class) {
            for (final Field f : c.getDeclaredFields()) {
                if (Modifier.isPublic(f.getModifiers())) {
                    String name = f.getName();
                    Tuple t = getTuple(properties, name);
                    t.field = f;
                }
             }
             c = c.getSuperclass();
        }
    }

    private static void findSetters(MethodList methodList, Map<String, Tuple> properties) {
        for (AnnotatedMethod am : methodList.
                hasReturnType(void.class).
                hasNumParams(1).
                nameStartsWith("set")) {
            Method m = am.getMethod();

            String name = getPropertyName(m);
            if (name == null)
                continue;

            Tuple t = getTuple(properties, name);
            t.setter = m;
        }
    }

    private static void findGetters(MethodList methodList, Map<String, Tuple> properties) {
        for (AnnotatedMethod am : methodList.
                hasNumParams(0).
                nameStartsWith("get")) {
            Method m = am.getMethod();

            String name = getPropertyName(m);
            if (name == null)
                continue;

            Tuple t = getTuple(properties, name);
            t.getter = m;
        }
    }

    private static String getPropertyName(Method m) {
        String name = m.getName();
        if (name.length() < 4) {
            return null;
        }
        return Character.toLowerCase(name.charAt(3)) + name.substring(4);
    }

    private static Map<String, BeanProperty> map(Class<?> beanClass, Map<String, Tuple> properties) {
        Map<String, BeanProperty> beanProperties = new HashMap<String, BeanProperty>();

        for (Entry<String, Tuple> e : properties.entrySet()) {
            String name = e.getKey();
            Tuple t = e.getValue();

            if ((t.getter != null) && (t.setter != null)) {
                // merge annotations
                List<Annotation> list = asList(t.setter.getAnnotations());
                mergeAnnotations(list, t.getter);

                if (t.field != null) {
                    mergeAnnotations(list, t.field);
                }

                Annotation[] annotations = list.toArray(new Annotation[0]);

                BeanProperty beanProperty = BeanProperty.fromProperty(beanClass, annotations, name, t.getter, t.setter);
                beanProperties.put(name, beanProperty);
            }
            else if (t.field != null) {
                Annotation[] annotations = t.field.getAnnotations();

                BeanProperty beanProperty = BeanProperty.fromPublicField(beanClass, annotations, name, t.field);
                beanProperties.put(name, beanProperty);
            }
        }
        return beanProperties;
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