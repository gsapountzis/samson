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

    private Converter<?> converter;

    MapBinder(BinderFactory factory, ElementRef ref) {
        super(BinderType.MAP, factory, ref);
    }

    /**
     * Bind map parameters, i.e. indexed by key.
     */
    @Override
    public void read(ParamNode<?> mapTree) {
        Annotation[] annotations = ref.element.annotations;
        MapTcp mapTcp = new MapTcp(ref.element.tcp);

        Map<?,?> map = (Map<?,?>) ref.accessor.get();
        if (map == null) {
            map = mapTcp.createInstance();
            ref.accessor.set(map);
        }

        for (ParamNode<?> valueTree : mapTree.getChildren()) {
            String stringKey = valueTree.getName();

            ElementRef valueRef = getElementRef(annotations, mapTcp, map, stringKey);
            if (valueRef == ElementRef.NULL_REF)
                continue;

            Binder binder = factory.getBinder(valueRef, valueTree.hasChildren());
            if (binder != Binder.NULL_BINDER) {
                binder.read(valueTree);
                node.addChild(binder.getNode());
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

    private Object getKey(TypeClassPair keyTcp, String stringKey) {
        if (keyTcp.c == String.class) {
            return stringKey;
        }
        else {
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

}
