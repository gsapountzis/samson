package samson.bind;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import samson.metadata.Element;

import com.google.common.base.Preconditions;

public class BeanNode extends StructureNode {

    private final Object bean;
    private final Map<String,TypedNode> values;

    BeanNode(Element element, Object bean, Map<String,TypedNode> values) {
        super(NodeType.BEAN, element);
        this.bean = Preconditions.checkNotNull(bean);
        this.values = Collections.unmodifiableMap(Preconditions.checkNotNull(values));
    }

    @Override
    public Object getObject() {
        return getBean();
    }

    public Object getBean() {
        return bean;
    }

    public Map<String,TypedNode> getValues() {
        return values;
    }

    public Set<String> getNames() {
        return values.keySet();
    }

    @Override
    public TypedNode getValue(String name) {
        return values.get(name);
    }

}
