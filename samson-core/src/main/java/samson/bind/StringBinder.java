package samson.bind;

import samson.metadata.ElementRef;

class StringBinder extends Binder {

    StringBinder(ElementRef ref) {
        super(null, BinderType.STRING, ref);
    }

    @Override
    public void read(BinderNode<?> node) {
    }

    @Override
    public ElementRef getElementRef(String name) {
        return ElementRef.NULL_REF;
    }

}
