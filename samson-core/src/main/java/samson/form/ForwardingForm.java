package samson.form;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.JForm;
import samson.convert.ConverterException;

public abstract class ForwardingForm<T> implements JForm<T> {

    protected abstract JForm<T> delegate();

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
    public Set<ConverterException> getConversionErrors() {
        return delegate().getConversionErrors();
    }

    @Override
    public Set<ConstraintViolation<T>> getConstraintViolations() {
        return delegate().getConstraintViolations();
    }

    @Override
    public List<String> getInfos() {
        return delegate().getInfos();
    }

    @Override
    public List<String> getErrors() {
        return delegate().getErrors();
    }

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
