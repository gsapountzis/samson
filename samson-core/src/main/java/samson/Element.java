package samson;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Immutable method parameter / bean property descriptor.
 */
public class Element {

    public static final Element NULL_ELEMENT = new Element(new Annotation[0], null, null);

    public final Annotation[] annotations;  // immutable
    public final TypeClassPair tcp;

    public final String name;               // @FormParam("...") @QueryParam("...")
    public final boolean encoded;           // @Encoded
    public final String defaultValue;       // @DefaultValue("...")

    public Element(Annotation[] annotations, TypeClassPair tcp, String name) {
        this(annotations, tcp, name, false, null);
    }

    public Element(Annotation[] annotations, Type type, Class<?> rawType, String name) {
        this(annotations, new TypeClassPair(type, rawType), name);
    }

    public Element(Annotation[] annotations, TypeClassPair tcp, String name, boolean encoded, String defaultValue) {
        this.annotations = annotations;
        this.tcp = tcp;

        this.name = name;
        this.encoded = encoded;
        this.defaultValue = defaultValue;
    }

    public Element(Annotation[] annotations, Type type, Class<?> rawType, String name, boolean encoded, String defaultValue) {
        this(annotations, new TypeClassPair(type, rawType), name, encoded, defaultValue);
    }

}
