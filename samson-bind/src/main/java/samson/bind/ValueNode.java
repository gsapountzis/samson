package samson.bind;

import java.util.Collection;
import java.util.List;

import samson.convert.ConverterException;
import samson.metadata.Element;

import com.google.common.base.Preconditions;

public abstract class ValueNode extends TypedNode {

    private final List<String> stringValues;

    ValueNode(NodeType type, Element element, List<String> stringValues) {
        super(type, element);
        this.stringValues = stringValues;
    }

    public List<String> getStringValues() {
        return stringValues;
    }

    @Override
    public TypedNode getChild(String name) {
        return null;
    }

    public static class ErrorValueNode extends ValueNode {

        private final ConverterException cause;

        ErrorValueNode(Element element, List<String> stringValues, ConverterException cause) {
            super(NodeType.ERROR, element, stringValues);
            this.cause = cause;
        }

        @Override
        public Object getObject() {
            return null;
        }

        public ConverterException getCause() {
            return cause;
        }

    }

    public static class SingleValueNode extends ValueNode {

        private final Object value;

        SingleValueNode(Element element, List<String> stringValues, Object value) {
            super(NodeType.VALUE, element, stringValues);
            this.value = value;
        }

        @Override
        public Object getObject() {
            return getValue();
        }

        public Object getValue() {
            return value;
        }

    }

    public static class MultiValueNode extends ValueNode {

        private final Collection<?> multivalue;

        MultiValueNode(Element element, List<String> stringValues, Collection<?> multivalue) {
            super(NodeType.MULTI_VALUE, element, stringValues);
            this.multivalue = Preconditions.checkNotNull(multivalue);
        }

        @Override
        public Object getObject() {
            return getMultiValue();
        }

        public Collection<?> getMultiValue() {
            return multivalue;
        }

    }

}
