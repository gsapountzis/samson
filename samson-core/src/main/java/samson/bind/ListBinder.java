package samson.bind;

import java.lang.annotation.Annotation;
import java.util.List;

import samson.metadata.Element;
import samson.metadata.Element.Accessor;
import samson.metadata.ElementRef;
import samson.metadata.ListTcp;

class ListBinder extends Binder {

    ListBinder(BinderFactory factory, ElementRef ref) {
        super(BinderType.LIST, factory, ref);
    }

    /**
     * Bind list parameters, i.e. indexed parameters.
     */
    @Override
    public void read(ParamNode<?> listTree) {
        Annotation[] annotations = ref.element.annotations;
        ListTcp listTcp = new ListTcp(ref.element.tcp);

        List<?> list = (List<?>) ref.accessor.get();
        if (list == null) {
            list = ListTcp.createInstance(listTcp.getTcp());
            ref.accessor.set(list);
        }

        for (ParamNode<?> itemTree : listTree.getChildren()) {
            String stringIndex = itemTree.getName();

            ElementRef itemRef = getElementRef(annotations, listTcp, list, stringIndex);
            if (itemRef == ElementRef.NULL_REF)
                continue;

            Binder binder = factory.getBinder(itemRef, itemTree.hasChildren());
            if (binder != Binder.NULL_BINDER) {
                binder.read(itemTree);
                node.addChild(binder.getNode());
            }
        }
    }

    @Override
    public ElementRef getElementRef(String name) {
        Annotation[] annotations = ref.element.annotations;
        ListTcp listTcp = new ListTcp(ref.element.tcp);

        List<?> list = (List<?>) ref.accessor.get();

        return getElementRef(annotations, listTcp, list, name);
    }

    private ElementRef getElementRef(Annotation[] annotations, ListTcp listTcp, List<?> list, String stringIndex) {
        int index = getIndex(stringIndex);
        if (index >= 0) {
            Element itemElement = listTcp.getItemElement(annotations, stringIndex);
            Accessor itemAccessor = ListTcp.createAccessor(list, index);
            return new ElementRef(itemElement, itemAccessor);
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
