package samson.bind;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.convert.Converter;
import samson.convert.ConverterException;
import samson.metadata.Element;
import samson.metadata.ElementAccessor;
import samson.metadata.ElementRef;
import samson.metadata.MapEntry;
import samson.metadata.TypeClassPair;

class MapBinder extends Binder {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapBinder.class);

    MapBinder(BinderFactory factory, ElementRef ref) {
        super(factory, BinderType.MAP, ref);
    }

    /**
     * Bind map parameters, i.e. indexed by key.
     */
    @Override
    public void read(BinderNode<?> node) {
        MapEntry mapEntry = MapEntry.fromMap(ref.element);
        Map<?,?> map = (Map<?,?>) ref.accessor.get();
        if (map == null) {
            map = createInstance(ref.element.tcp);
            ref.accessor.set(map);
        }

        for (BinderNode<?> child : node.getChildren()) {
            String stringKey = child.getName();
            ElementRef childRef = getChildRef(mapEntry, map, stringKey);
            child.setRef(childRef);

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            binder.read(child);
        }
    }

    @Override
    public ElementRef getChildRef(String name) {
        MapEntry mapEntry = MapEntry.fromMap(ref.element);
        Map<?,?> map = (Map<?,?>) ref.accessor.get();

        ElementRef childRef = getChildRef(mapEntry, map, name);
        return childRef;
    }

    private ElementRef getChildRef(MapEntry mapEntry, Map<?,?> map, String stringKey) {
        Object key = getKey(mapEntry.keyTcp, stringKey);
        if (key != null) {
            Element valueElement = mapEntry.createElement(stringKey);
            ElementAccessor valueAccessor = MapEntry.createAccessor(map, key);
            return new ElementRef(valueElement, valueAccessor);
        }
        else {
            LOGGER.warn("Invalid map key: {}", stringKey);
            return ElementRef.NULL_REF;
        }
    }

    private Converter<?> converter;

    private Object getKey(TypeClassPair keyTcp, String stringKey) {
        if (converter == null) {
            converter = factory.getConverter(keyTcp, null);
            if (converter == null) {
                throw new RuntimeException("Unsupported map key type");
            }
        }
        try {
            return converter.fromString(stringKey);
        } catch (ConverterException ex) {
            return null;
        }
    }

    public static Map<?,?> createInstance(TypeClassPair tcp) {
        Class<?> mapClass = tcp.c;

        if (!Map.class.isAssignableFrom(mapClass)) {
            throw new IllegalArgumentException();
        }

        if (mapClass.isInterface()) {
            if (mapClass == Map.class) {
                mapClass = HashMap.class;
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

}
