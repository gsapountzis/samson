package samson.bind;

import java.lang.annotation.Annotation;
import java.util.List;

import samson.metadata.Element;
import samson.metadata.Element.Accessor;
import samson.metadata.ElementRef;
import samson.metadata.ListTcp;
import samson.metadata.TypeClassPair;

class ListBinder extends Binder {

    ListBinder(BinderFactory factory, ElementRef ref) {
        super(BinderType.LIST, factory, ref);
    }

    /**
     * Bind list parameters, i.e. indexed parameters.
     */
    @Override
    public void read(ParamNode<?> listTree) {
        ListTcp listTcp = new ListTcp(ref.element.tcp);
        Annotation[] annotations = ref.element.annotations;

        List<?> list = (List<?>) ref.accessor.get();
        if (list == null) {
            list = ListTcp.createInstance(listTcp.getTcp());
            ref.accessor.set(list);
        }

        for (ParamNode<?> elemTree : listTree.getChildren()) {
            String stringIndex = elemTree.getName();

            ElementRef elemRef = getElementRef(annotations, listTcp, list, stringIndex);
            if (elemRef == ElementRef.NULL_REF)
                continue;

            Binder binder = factory.getBinder(elemRef, elemTree.hasChildren());
            if (binder != Binder.NULL_BINDER) {
                binder.read(elemTree);
                node.addChild(binder.getNode());
            }
        }
    }

    @Override
    public ElementRef getElementRef(String name) {
        ListTcp listTcp = new ListTcp(ref.element.tcp);
        Annotation[] annotations = ref.element.annotations;

        List<?> list = (List<?>) ref.accessor.get();

        return getElementRef(annotations, listTcp, list, name);
    }

    private ElementRef getElementRef(Annotation[] annotations, ListTcp listTcp, List<?> list, String stringIndex) {
        int index = getIndex(stringIndex);
        if (index >= 0) {
            TypeClassPair elemTcp = listTcp.getElementTcp();
            Element elemElement = new Element(annotations, elemTcp, stringIndex);
            Accessor elemAccessor = ListTcp.createAccessor(list, index);
            return new ElementRef(elemElement, elemAccessor);
        }
        else {
            return ElementRef.NULL_REF;
        }
    }

    private int getIndex(String stringIndex) {
        try {
            return Integer.parseInt(stringIndex);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

}
