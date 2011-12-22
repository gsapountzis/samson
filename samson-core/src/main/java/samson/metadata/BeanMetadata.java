package samson.metadata;

import java.util.Map;

public class BeanMetadata {

    private final Class<?> beanClass;
    private final Map<String, BeanProperty> properties;

    public BeanMetadata(Class<?> beanClass, Map<String, BeanProperty> properties) {
        this.beanClass = beanClass;
        this.properties = properties;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Map<String, BeanProperty> getProperties() {
        return properties;
    }

}
