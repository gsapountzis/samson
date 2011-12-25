package samson.form;

import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.JForm;

class PathForm implements JForm<Object> {

    private final JForm<?> delegate;
    private final String path;

    PathForm(JForm<?> delegate, String path) {
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
    public Set<Throwable> getConversionErrors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<ConstraintViolation<Object>> getConstraintViolations() {
        throw new UnsupportedOperationException();
    }

    // -- Path

    @Override
    public JForm<?> path(String suffix) {
        return new PathForm(this, path + suffix);
    }

    @Override
    public JForm<?> dot(String property) {
        return new PathForm(this, path + "." + property);
    }

    @Override
    public JForm<?> index(String index) {
        return new PathForm(this, path + "[" + index + "]");
    }

    @Override
    public JForm<?> index(int index) {
        return index(Integer.toString(index));
    }

    @Override
    public String getPath() {
        return path;
    }

    // -- Field

    @Override
    public Field getField() {
        return delegate.getField(path);
    }

    @Override
    public Field getField(final String path) {
        return delegate.getField(path);
    }

    @Override
    public Messages getMessages() {
        return delegate.getMessages(path);
    }

    @Override
    public Messages getMessages(final String path) {
        return delegate.getMessages(path);
    }

    @Override
    public void info(String msg) {
        delegate.info(path, msg);
    }

    @Override
    public void info(String path, String msg) {
        delegate.info(path, msg);
    }

    @Override
    public void error(String msg) {
        delegate.error(path, msg);
    }

    @Override
    public void error(String path, String msg) {
        delegate.error(path, msg);
    }

}
