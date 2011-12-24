package samson.metadata;

import java.util.Map;

import samson.TypeClassPair;

public class BeanTcp {

    private final TypeClassPair tcp;
    private final Map<String, BeanProperty> properties;

    public BeanTcp(TypeClassPair tcp, Map<String, BeanProperty> properties) {
        this.tcp = tcp;
        this.properties = properties;
    }

    public TypeClassPair getTcp() {
        return tcp;
    }

    public boolean hasProperty(String propertyName) {
        return properties.containsKey(propertyName);
    }

    public BeanProperty getProperty(String propertyName) {
        BeanProperty property = properties.get(propertyName);
        if (property == null) {
            throw new IllegalArgumentException();
        }

        return property;
    }

    public Map<String, BeanProperty> getProperties() {
        return properties;
    }

    public Object createInstance() {
        Class<?> beanClass = tcp.c;

        try {
            return beanClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

}
