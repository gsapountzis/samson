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

}
