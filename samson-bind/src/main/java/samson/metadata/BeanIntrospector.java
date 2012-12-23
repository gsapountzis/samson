package samson.metadata;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class BeanIntrospector {

    static BeanMetadata introspect(TypeClassPair tcp) {
        return new BeanIntrospector().apply(tcp);
    }

    private BeanIntrospector() {
    }

    private BeanMetadata apply(TypeClassPair tcp) {

        final Class<?> clazz = tcp.c;

        findFields(clazz);

        findGetters(clazz);
        findSetters(clazz);

        return new BeanMetadata(tcp, createProperties(tcp));
    }

    private Map<String, Field> fields = new HashMap<String, Field>();

    private Map<String, Method> getters = new HashMap<String, Method>();
    private Map<String, Method> setters = new HashMap<String, Method>();

    private void findFields(Class<?> c) {
        while (c != Object.class) {

            for (Field f : c.getDeclaredFields()) {
                if (isPublicWritableField(f)) {
                    String name = f.getName();
                    if (fields.containsKey(name)) {
                        // superclass does not override fields from subclass
                        continue;
                    }
                    fields.put(name, f);
                }
            }

            c = c.getSuperclass();
        }
    }

    private static Field findFieldForProperty(Method getter, String property) {
        // search only the declaring class of the getter
        Class<?> c = getter.getDeclaringClass();

        for (Field f : c.getDeclaredFields()) {
            if (isWritableField(f)) {
                String name = f.getName();
                if (name.equals(property)) {
                    return f;
                }
            }
        }
        return null;
    }

    private void findGetters(Class<?> c) {
        Method[] methods = c.getMethods();
        for (Method m : methods) {
            if (isGetter(m)) {
                String property = getPropertyName(m.getName());
                getters.put(property, m);
            }
        }
    }

    private void findSetters(Class<?> c) {
        Method[] methods = c.getMethods();
        for (Method m : methods) {
            if (isSetter(m)) {
                String property = getPropertyName(m.getName());
                setters.put(property, m);
            }
        }
    }

    private static boolean isPublicWritableField(Field f) {
        int modifiers = f.getModifiers();

        if ((modifiers & Modifier.PUBLIC) == 0) {
            return false;
        }

        if ((modifiers & (Modifier.STATIC | Modifier.FINAL | Modifier.NATIVE)) != 0) {
            return false;
        }

        return true;
    }

    private static boolean isWritableField(Field f) {
        int modifiers = f.getModifiers();

        if ((modifiers & (Modifier.STATIC | Modifier.FINAL | Modifier.NATIVE)) != 0) {
            return false;
        }

        return true;
    }

    private static boolean isGetter(Method m) {
        int modifiers = m.getModifiers();
        Class<?> returnType = m.getReturnType();
        Class<?>[] parameterTypes = m.getParameterTypes();
        String name = m.getName();

        if ((modifiers & (Modifier.STATIC | Modifier.NATIVE)) != 0) {
            return false;
        }

        if ((returnType != void.class) && (parameterTypes.length == 0)) {
            if (name.startsWith("get") && (name.length() > 3)) {
                return true;
            }
            else if ((returnType == boolean.class || returnType == Boolean.class) && name.startsWith("is") && (name.length() > 2)) {
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

    private static boolean isSetter(Method m) {
        int modifiers = m.getModifiers();
        Class<?> returnType = m.getReturnType();
        Class<?>[] parameterTypes = m.getParameterTypes();
        String name = m.getName();

        if ((modifiers & (Modifier.STATIC | Modifier.NATIVE)) != 0) {
            return false;
        }

        if ((returnType == void.class) && (parameterTypes.length == 1)) {
            if (name.startsWith("set") && (name.length() > 3)) {
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
        if (name.startsWith("set") || name.startsWith("get")) {
            prefix = 3;
        }
        else if (name.startsWith("is")) {
            prefix = 2;
        }
        else {
            throw new IllegalArgumentException();
        }

        return Character.toLowerCase(name.charAt(prefix)) + name.substring(prefix + 1);
    }

    private Map<String, BeanProperty> createProperties(TypeClassPair beanTcp) {
        Map<String, BeanProperty> beanProperties = new HashMap<String, BeanProperty>();

        for (Entry<String, Method> e : getters.entrySet()) {
            String name = e.getKey();
            Method getter = e.getValue();

            Method setter = setters.get(name);
            if (setter == null) {
                continue;
            }

            Field field = findFieldForProperty(getter, name);
            BeanProperty beanProperty = BeanProperty.fromProperty(beanTcp, name, getter, setter, field);
            beanProperties.put(name, beanProperty);
        }

        for (Entry<String, Field> e : fields.entrySet()) {
            String name = e.getKey();
            Field field = e.getValue();

            if (setters.containsKey(name)) {
                // public fields do not override bean properties
                continue;
            }

            BeanProperty beanProperty = BeanProperty.fromPublicField(beanTcp, name, field);
            beanProperties.put(name, beanProperty);
        }

        return beanProperties;
    }

}