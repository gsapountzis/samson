package samson.form;

import java.util.List;
import java.util.Map;

import samson.JForm;

public abstract class ForwardingForm<T> implements JForm<T> {

    protected abstract JForm<T> delegate();

    // -- Path

    @Override
    public String getPath() {
        return delegate().getPath();
    }

    @Override
    public JForm<?> path(String path) {
        return delegate().path(path);
    }

    @Override
    public JForm<?> dot(String property) {
        return delegate().dot(property);
    }

    @Override
    public JForm<?> index(String index) {
        return delegate().index(index);
    }

    @Override
    public JForm<?> index(int index) {
        return delegate().index(index);
    }

    // -- Form

    @Override
    public T get() {
        return delegate().get();
    }

    @Override
    public boolean hasErrors() {
        return delegate().hasErrors();
    }

    @Override
    public Map<String, List<String>> getInfos() {
        return delegate().getInfos();
    }

    @Override
    public Map<String, List<String>> getErrors() {
        return delegate().getErrors();
    }

    // -- Field

    @Override
    public Field getField() {
        return delegate().getField();
    }

    @Override
    public Messages getMessages() {
        return delegate().getMessages();
    }

    @Override
    public void info(String msg) {
        delegate().info(msg);
    }

    @Override
    public void error(String msg) {
        delegate().error(msg);
    }

}
