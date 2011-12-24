package samson.bind;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import samson.convert.Converter;
import samson.convert.ConverterException;
import samson.metadata.Element;
import samson.metadata.Element.Accessor;
import samson.metadata.ElementRef;
import samson.metadata.MapTcp;
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
        Annotation[] annotations = ref.element.annotations;
        MapTcp mapTcp = new MapTcp(ref.element.tcp);
        Map<?,?> map = (Map<?,?>) ref.accessor.get();
        if (map == null) {
            map = mapTcp.createInstance();
            ref.accessor.set(map);
        }

        for (BinderNode<?> child : node.getChildren()) {
            String stringKey = child.getName();

            ElementRef childRef = getElementRef(annotations, mapTcp, map, stringKey);
            if (childRef == ElementRef.NULL_REF)
                continue;

            Binder binder = factory.getBinder(childRef, child.hasChildren());
            if (binder != Binder.NULL_BINDER) {
                binder.read(child);
                child.setBinder(binder);
            }
        }
    }

    @Override
    public ElementRef getElementRef(String name) {
        Annotation[] annotations = ref.element.annotations;
        MapTcp mapTcp = new MapTcp(ref.element.tcp);
        Map<?,?> map = (Map<?,?>) ref.accessor.get();

        return getElementRef(annotations, mapTcp, map, name);
    }

    private ElementRef getElementRef(Annotation[] annotations, MapTcp mapTcp, Map<?,?> map, String stringKey) {
        Object key = getKey(mapTcp.getKeyTcp(), stringKey);
        if (key != null) {
            Element valueElement = mapTcp.createValueElement(annotations, stringKey);
            Accessor valueAccessor = MapTcp.createValueAccessor(map, key);
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

}
