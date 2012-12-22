package samson.bind;

import samson.metadata.ElementRef;

public abstract class Binder {

    final BinderFactory factory;
    final BinderType type;
    final ElementRef ref;

    Binder(BinderFactory factory, BinderType type, ElementRef ref) {
        this.factory = factory;
        this.type = type;
        this.ref = ref;
    }

    public BinderType getType() {
        return type;
    }

    public ElementRef getRef() {
        return ref;
    }

    public abstract ElementRef getChildRef(String name);

    public abstract void read(BinderNode<?> node);

//  public abstract void write(BinderNode<?> node);

    public static final Binder NULL_BINDER = new Binder(null, BinderType.NULL, ElementRef.NULL_REF) {

        @Override
        public ElementRef getChildRef(String name) {
            return ElementRef.NULL_REF;
        }

        @Override
        public void read(BinderNode<?> node) {
        }

    };

}
