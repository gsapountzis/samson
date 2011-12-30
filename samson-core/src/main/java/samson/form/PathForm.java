package samson.form;

import java.util.List;
import java.util.Map;

import samson.JForm;
import samson.form.Property.Path;
import samson.metadata.ElementRef;

class PathForm implements JForm<Object> {

    private final Form<?> delegate;
    private final String param;

    PathForm(Form<?> delegate, String param) {
        this.delegate = delegate;
        this.param = param;
    }

    // -- Form

    @Override
    public Object get() {
        Path path = Path.createPath(param);
        List<ElementRef> refs = delegate.getPathRef(path);
        int size = refs.size();
        ElementRef ref = refs.get(size - 1);
        return ref.accessor.get();
    }

    @Override
    public boolean hasErrors() {
        Path path = Path.createPath(param);
        FormNode node = delegate.getFormNode(path);
        return node.isTreeError();
    }

    @Override
    public Map<String, List<String>> getInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, List<String>> getErrors() {
        throw new UnsupportedOperationException();
    }

    // -- Path

    @Override
    public String getPath() {
        return param;
    }

    @Override
    public JForm<?> path(String path) {
        return delegate.path(path);
    }

    @Override
    public JForm<?> dot(String property) {
        return new PathForm(delegate, param + "." + property);
    }

    @Override
    public JForm<?> index(String index) {
        return new PathForm(delegate, param + "[" + index + "]");
    }

    @Override
    public JForm<?> index(int index) {
        return index(Integer.toString(index));
    }

    // -- Field

    @Override
    public Field getField() {
        return delegate.getField(param);
    }

    @Override
    public Messages getMessages() {
        return delegate.getField(param);
    }

    @Override
    public void info(String msg) {
        delegate.info(param, msg);
    }

    @Override
    public void error(String msg) {
        delegate.error(param, msg);
    }

}
