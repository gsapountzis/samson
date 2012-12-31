package samson.bind;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.metadata.ElementRef;
import samson.metadata.TypeClassPair;

class StringBinder extends Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringBinder.class);

    StringBinder(BinderFactory factory, ElementRef ref) {
        super(factory, BinderType.STRING, ref);
    }

    @Override
    public ElementRef getChildRef(String name) {
        throw new IllegalStateException();
    }

    @Override
    public void read(BinderNode<?> node) {
        ConversionResult conversion = factory.fromStringList(ref.element, node.getStringValues());
        if (conversion != null) {
            if (conversion.isError()) {
                node.setConversionFailure(conversion.getCause());
            }
            else {
                ref.accessor.set(conversion.getValue());
            }
        }
        else {
            TypeClassPair tcp = ref.element.tcp;
            Class<?> clazz = tcp.c;
            LOGGER.warn("Cannot convert {} from string values, using default ctor", clazz);
            ref.accessor.set(createDefault(tcp));
        }
    }

    private Object createDefault(TypeClassPair tcp) {
        Class<?> clazz = tcp.c;
        try {
            if (Set.class.isAssignableFrom(clazz)) {
                return ListBinder.createSet(tcp);
            }
            else if (List.class.isAssignableFrom(clazz)) {
                return ListBinder.createInstance(tcp);
            }
            else if (Map.class.isAssignableFrom(clazz)) {
                return MapBinder.createInstance(tcp);
            }
            else {
                if (factory.binderTypeIsValid(BinderType.BEAN, tcp, null)) {
                    return BeanBinder.createInstance(tcp);
                }
                else {
                    return null;
                }
            }
        }
        catch (RuntimeException e) {
            LOGGER.warn("Cannot instantiate {}", clazz);
            return null;
        }
    }

}
