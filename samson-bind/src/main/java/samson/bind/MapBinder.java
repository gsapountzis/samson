package samson.bind;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.convert.Converter;
import samson.convert.ConverterException;
import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.ResolvedMapType;
import samson.metadata.TypeClassPair;

class MapBinder extends Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapBinder.class);

    private final ResolvedMapType type;

    private final Converter<?> keyConverter;

    MapBinder(BinderFactory factory, Element element) {
        super(factory, element);
        this.type = new ResolvedMapType(element.tcp);

        this.keyConverter = factory.getConverter(type.getKeyTcp(), null);
        if (keyConverter == null) {
            throw new RuntimeException("Unsupported map key type");
        }
    }

    @Override
    public TypedNode child(String name, Object object) {
        Map<?,?> map = (Map<?,?>) object;
        Object key = getKey(name);
        if (key != null) {
            ElementAccessor accessor = createAccessor(map, key);
            return new AnyNode(type.getValue(), accessor.get());
        }
        else {
            LOGGER.warn("Invalid map key: {}", name);
            return NullNode.INSTANCE;
        }
    }

    /**
     * Bind map parameters, i.e. indexed by key.
     */
    @Override
    public TypedNode parse(UntypedNode untypedNode, Object object) {
        Map<?,?> map = (Map<?,?>) object;
        if (map == null) {
            map = createMap(element.tcp);
        }

        Map<String,TypedNode> nodes = new LinkedHashMap<String,TypedNode>();

        for (Entry<String, UntypedNode> e : untypedNode.getChildren().entrySet()) {
            String name = e.getKey();
            UntypedNode untypedChild = e.getValue();

            Object key = getKey(name);
            if (key != null) {
                ElementAccessor accessor = createAccessor(map, key);
                ElementAccessor childAccessor = createAccessor(nodes, name);

                Binder binder = factory.getBinder(type.getValue(), untypedChild.hasChildren());
                TypedNode child = binder.parse(untypedChild, accessor.get());

                accessor.set(child.getObject());
                childAccessor.set(child);
            }
            else {
                LOGGER.warn("Invalid map key: {}", name);
            }
        }

        return new MapNode(element, map, nodes);
    }

    private Object getKey(String stringKey) {
        try {
            return keyConverter.fromString(stringKey);
        } catch (ConverterException ex) {
            return null;
        }
    }

    public static Map<?,?> createMap(TypeClassPair tcp) {
        Class<?> mapClass = tcp.c;

        if (!Map.class.isAssignableFrom(mapClass)) {
            throw new IllegalArgumentException();
        }

        if (mapClass.isInterface()) {
            if (mapClass == Map.class) {
                mapClass = LinkedHashMap.class;
            }
            else {
                throw new RuntimeException("Unknown map interface");
            }
        }

        try {
            return (Map<?,?>) mapClass.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ElementAccessor createAccessor(final Map<?, ?> map, final Object key) {
        if (map == null) {
            return ElementAccessor.NULL_ACCESSOR;
        }

        return new ElementAccessor() {

            @SuppressWarnings("unchecked")
            @Override
            public void set(Object value) {
                ((Map<Object, Object>) map).put(key, value);
            }

            @Override
            public Object get() {
                return map.get(key);
            }
        };
    }

}
