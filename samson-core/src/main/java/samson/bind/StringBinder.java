package samson.bind;

import java.util.List;

import samson.metadata.ElementRef;

class StringBinder extends Binder {

    StringBinder(ElementRef ref) {
        super(BinderType.STRING, null, ref);
    }

    @Override
    public void read(ParamNode<?> tree) {
        List<String> params = tree.getStringValues();
        node.setStringValues(params);
    }

    @Override
    public ElementRef getElementRef(String name) {
        return ElementRef.NULL_REF;
    }

}
