package samson.bind;

import samson.metadata.ElementRef;

class StringBinder extends Binder {

    StringBinder(ElementRef ref) {
        super(null, BinderType.STRING, ref);
    }

    @Override
    public ElementRef getChildRef(String name) {
        throw new IllegalStateException();
    }

    @Override
    public void read(BinderNode<?> node) {
    }

}
