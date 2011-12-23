package samson.bind;

import samson.metadata.ElementRef;

public abstract class Binder {

    final BinderType type;
    final BinderFactory factory;
    final ElementRef ref;
    final BinderNode node;

    Binder(BinderType type, BinderFactory factory, ElementRef ref) {
        this.type = type;
        this.factory = factory;
        this.ref = ref;

        this.node = new BinderNode(this, ref.element.name);
    }

    public BinderType getType() {
        return type;
    }

    public ElementRef getElementRef() {
        return ref;
    }

    public BinderNode getNode() {
        return node;
    }

    public abstract ElementRef getElementRef(String name);

    public abstract void read(ParamNode<?> node);

//  public abstract void write(ParamNode<?> node);

    static final Binder NULL_BINDER = new Binder(BinderType.NULL, null, ElementRef.NULL_REF) {

        @Override
        public ElementRef getElementRef(String name) {
            return ElementRef.NULL_REF;
        }

        @Override
        public void read(ParamNode<?> node) {
        }

    };

}
