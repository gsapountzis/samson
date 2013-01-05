package samson.bind;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import samson.metadata.Element;
import samson.metadata.TypeClassPair;

class ValueBinder extends Binder {

    ValueBinder(BinderFactory factory, Element element) {
        super(factory, element);
    }

    @Override
    public TypedNode child(String name, Object object) {
        throw new IllegalStateException();
    }

    @Override
    public TypedNode parse(UntypedNode untypedNode, Object object) {
        TypeClassPair tcp = element.tcp;
        List<String> values = untypedNode.getStringValues();

        ConversionResult conversion = factory.fromStringList(element, values);
        if (conversion.isError()) {
            return new ValueNode.ErrorValueNode(element, values, conversion.getCause());
        }
        else {
            if (tcp.c == List.class || tcp.c == Set.class || tcp.c == SortedSet.class) {
                return new ValueNode.MultiValueNode(element, values, (Collection<?>) conversion.getValue());
            }
            else {
                return new ValueNode.SingleValueNode(element, values, conversion.getValue());
            }
        }
    }

}
