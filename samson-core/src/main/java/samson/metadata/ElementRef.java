package samson.metadata;

import samson.Element;

public class ElementRef {

    public static final ElementRef NULL_REF = new ElementRef(Element.NULL_ELEMENT, ElementAccessor.NULL_ACCESSOR);

    public final Element element;
    public final ElementAccessor accessor;

    public ElementRef(Element element, ElementAccessor accessor) {
        this.element = element;
        this.accessor = accessor;
    }

}
