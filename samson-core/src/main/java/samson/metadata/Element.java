package samson.metadata;

import java.lang.annotation.Annotation;

/**
 * Immutable method parameter / bean property descriptor.
 */
public class Element {

    public static final Element NULL_ELEMENT = new Element(new Annotation[0], null, JaxrsAnnotations.NULL);

    public final Annotation[] annotations;  // immutable
    public final TypeClassPair tcp;

    public final JaxrsAnnotations jaxrs;

    public Element(Annotation[] annotations, TypeClassPair tcp) {
        this(annotations, tcp, JaxrsAnnotations.NULL);
    }

    public Element(Annotation[] annotations, TypeClassPair tcp, JaxrsAnnotations jaxrs) {
        this.annotations = annotations;
        this.tcp = tcp;
        this.jaxrs = jaxrs;
    }

    public static class JaxrsAnnotations {

        public static final JaxrsAnnotations NULL = new JaxrsAnnotations(null, false, null);

        /** @FormParam("...") @QueryParam("...") */
        public final String name;

        /** @Encoded */
        public final boolean encoded;

        /** @DefaultValue("...") */
        public final String defaultValue;

        public JaxrsAnnotations(String name) {
            this(name, false, null);
        }

        public JaxrsAnnotations(String name, boolean encoded, String defaultValue) {
            this.name = name;
            this.encoded = encoded;
            this.defaultValue = defaultValue;
        }

    }

}
