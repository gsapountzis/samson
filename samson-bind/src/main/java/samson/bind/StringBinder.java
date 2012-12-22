package samson.bind;

import samson.metadata.ElementRef;

class StringBinder extends Binder {

    StringBinder(BinderFactory factory, ElementRef ref) {
        super(factory, BinderType.STRING, ref);
    }

    @Override
    public ElementRef getChildRef(String name) {
        throw new IllegalStateException();
    }

    @Override
    public void read(BinderNode<?> node) {
        ConversionResult conversion = factory.fromStringList(ref.element, node.getStringValues());
        if (conversion != null) {
            if (conversion.isError()) {
                node.setConversionFailure(conversion.getCause());
            }
            else {
                ref.accessor.set(conversion.getValue());
            }
        }
    }

}
