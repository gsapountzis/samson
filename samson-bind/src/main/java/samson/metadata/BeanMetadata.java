package samson.metadata;

import java.util.Map;

public class BeanMetadata {

    private final TypeClassPair tcp;
    private final Map<String, BeanProperty> properties;

    public BeanMetadata(TypeClassPair tcp, Map<String, BeanProperty> properties) {
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
        return properties.get(propertyName);
    }

    public Map<String, BeanProperty> getProperties() {
        return properties;
    }

    public ElementAccessor createAccessor(final Object bean, final String propertyName) {
        if (bean == null) {
            return ElementAccessor.NULL_ACCESSOR;
        }

        Class<?> clazz = tcp.c;
        if (!clazz.isInstance(bean)) {
            throw new IllegalArgumentException();
        }
        if (!this.hasProperty(propertyName)) {
            throw new IllegalArgumentException();
        }

        final BeanProperty property = this.getProperty(propertyName);

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

}
