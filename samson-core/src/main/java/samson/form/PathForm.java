package samson.form;

import java.util.List;
import java.util.Map;

import samson.JForm;

class PathForm implements JForm<Object> {

    private final Form<?> delegate;
    private final String path;

    PathForm(Form<?> delegate, String path) {
        this.delegate = delegate;
        this.path = path;
    }

    // -- Form

    @Override
    public Object get() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasErrors() {
        throw new UnsupportedOperationException();
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
        return path;
    }

    @Override
    public JForm<?> path(String path) {
        return delegate.path(path);
    }

    @Override
    public JForm<?> dot(String property) {
        return new PathForm(delegate, path + "." + property);
    }

    @Override
    public JForm<?> index(String index) {
        return new PathForm(delegate, path + "[" + index + "]");
    }

    @Override
    public JForm<?> index(int index) {
        return index(Integer.toString(index));
    }

    // -- Field

    @Override
    public Field getField() {
        return delegate.getField(path);
    }

    @Override
    public Messages getMessages() {
        return delegate.getField(path);
    }

    @Override
    public void info(String msg) {
        delegate.info(path, msg);
    }

    @Override
    public void error(String msg) {
        delegate.error(path, msg);
    }

}
