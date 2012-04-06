package samson.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import samson.metadata.Errors.Closure;

public class BeanMetadataCache {

    private final ConcurrentMap<Class<?>, BeanMetadata> cache = new ConcurrentHashMap<Class<?>, BeanMetadata>();

    public BeanMetadata get(final TypeClassPair tcp) {
        if (tcp == null) {
            return null;
        }
        final Class<?> clazz = tcp.c;

        BeanMetadata bean = cache.get(clazz);
        if (bean == null) {
            final BeanMetadata newBean = Errors.processWithErrors(new Closure<BeanMetadata>() {
                @Override
                public BeanMetadata f() {
                    return BeanIntrospector.createBeanMetadata(tcp);
                }
            });
            bean = cache.putIfAbsent(clazz, newBean);
            if (bean == null) {
                bean = newBean;
            }
        }

        return bean;
    }

    static class BeanIntrospector {

        static BeanMetadata createBeanMetadata(TypeClassPair tcp) {
            final Class<?> clazz = tcp.c;

            Map<String, Tuple> properties = new HashMap<String, Tuple>();

            checkClass(clazz);

            findFields(clazz, properties);

            findSetters(clazz, properties);

            findGetters(clazz, properties);

            return new BeanMetadata(tcp, map(tcp, properties));
        }

        private static void checkClass(Class<?> clazz) {
            final int modifiers = clazz.getModifiers();

            if (!Modifier.isPublic(modifiers)) {
                Errors.nonPublicClass(clazz);
            }

            /*
            if (Modifier.isAbstract(modifiers)) {
                if (Modifier.isInterface(modifiers)) {
                    Errors.interfaceClass(clazz);
                } else {
                    Errors.abstractClass(clazz);
                }
            }
            */

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
                    String name = f.getName();

                    Tuple t = getTuple(properties, name);
                    t.field = f;
                 }
                 c = c.getSuperclass();
            }
        }

        private static void findSetters(Class<?> c, Map<String, Tuple> properties) {
            Method[] methods = c.getMethods();
            for (Method m : methods) {
                if (!isSetter(m)) {
                    continue;
                }

                String property = getPropertyName(m.getName());
                if (property != null) {
                    Tuple t = getTuple(properties, property);
                    t.setter = m;
                }
            }
        }

        private static void findGetters(Class<?> c, Map<String, Tuple> properties) {
            Method[] methods = c.getMethods();
            for (Method m : methods) {
                if (!isGetter(m)) {
                    continue;
                }

                String property = getPropertyName(m.getName());
                if (property != null) {
                    Tuple t = getTuple(properties, property);
                    t.getter = m;
                }
            }
        }

        private static boolean isSetter(Method m) {
            Class<?> returnType = m.getReturnType();
            Class<?>[] parameterTypes = m.getParameterTypes();
            String name = m.getName();

            if ((returnType == void.class) && (parameterTypes.length == 1)) {
                if (name.startsWith("set")) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        private static boolean isGetter(Method m) {
            Class<?> returnType = m.getReturnType();
            Class<?>[] parameterTypes = m.getParameterTypes();
            String name = m.getName();

            if ((returnType != void.class) && (parameterTypes.length == 0)) {
                if (name.startsWith("get")) {
                    return true;
                }
                else if ((returnType == boolean.class || returnType == Boolean.class) && name.startsWith("is")) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return false;
            }
        }

        private static String getPropertyName(String name) {
            int prefix;

            if (name.startsWith("set")) {
                prefix = 3;
            }
            else if (name.startsWith("get")) {
                prefix = 3;
            }
            else if (name.startsWith("is")) {
                prefix = 2;
            }
            else {
                return null;
            }

            if (name.length() < prefix + 1) {
                return null;
            }
            return Character.toLowerCase(name.charAt(prefix)) + name.substring(prefix + 1);
        }

        private static Map<String, BeanProperty> map(TypeClassPair beanTcp, Map<String, Tuple> properties) {
            Map<String, BeanProperty> beanProperties = new HashMap<String, BeanProperty>();

            for (Entry<String, Tuple> e : properties.entrySet()) {
                String name = e.getKey();
                Tuple t = e.getValue();

                if ((t.getter != null) && (t.setter != null)) {
                    BeanProperty beanProperty = BeanProperty.fromProperty(beanTcp, name, t.getter, t.setter, t.field);
                    beanProperties.put(name, beanProperty);
                }
                else if (t.field != null) {
                    int modifiers = t.field.getModifiers();
                    if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
                        BeanProperty beanProperty = BeanProperty.fromPublicField(beanTcp, name, t.field);
                        beanProperties.put(name, beanProperty);
                    }
                }
            }
            return beanProperties;
        }

    }

}
