package samson.metadata;

public interface ElementAccessor {

    public static final ElementAccessor NULL_ACCESSOR = new ElementAccessor() {

        @Override
        public void set(Object value) {
        }

        @Override
        public Object get() {
            return null;
        }
    };

    Object get();

    void set(Object value);
}