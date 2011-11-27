package samson.metadata;

public class ElementRef {

    public static final ElementRef NULL_REF = new ElementRef(Element.NULL_ELEMENT, Element.Accessor.NULL_ACCESSOR);

    public final Element element;
    public final Element.Accessor accessor;

    public ElementRef(Element element, Element.Accessor accessor) {
        this.element = element;
        this.accessor = accessor;
    }

}
