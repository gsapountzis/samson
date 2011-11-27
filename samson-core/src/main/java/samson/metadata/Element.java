package samson.metadata;

import java.lang.annotation.Annotation;

/**
 * Immutable method parameter / bean property descriptor.
 */
public class Element {

    public static final Element NULL_ELEMENT = new Element(new Annotation[0], null, null);

    public final Annotation[] annotations; /* immutable */
    public final TypeClassPair tcp;
    public final String name;

    public final boolean encoded;
    public final String defaultValue;

    public Element(Annotation[] annotations, TypeClassPair tcp, String name) {
        this(annotations, tcp, name, false, null);
    }

    public Element(Annotation[] annotations, TypeClassPair tcp, String name, boolean encoded, String defaultValue) {
        this.annotations = annotations;
        this.tcp = tcp;
        this.name = name;

        this.encoded = encoded;
        this.defaultValue = defaultValue;
    }

    public static interface Accessor {

        public static final Accessor NULL_ACCESSOR = new Accessor() {

            @Override
            public void set(Object value) {
            }

            @Override
            public Object get() {
                return null;
            }
        };

        Object get();

        void set(Object value);
    }

}
