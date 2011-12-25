package samson;

import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.convert.ConverterException;

public class ForwardingForm<T> implements JForm<T> {

    protected final JForm<T> delegate;

    public ForwardingForm(JForm<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {
        return delegate.get();
    }

    @Override
    public boolean hasErrors() {
        return delegate.hasErrors();
    }

    @Override
    public Set<ConverterException> getConversionErrors() {
        return delegate.getConversionErrors();
    }

    @Override
    public Set<ConstraintViolation<T>> getConstraintViolations() {
        return delegate.getConstraintViolations();
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public JForm<?> path(String path) {
        return delegate.path(path);
    }

    @Override
    public JForm<?> dot(String property) {
        return delegate.dot(property);
    }

    @Override
    public JForm<?> index(String index) {
        return delegate.index(index);
    }

    @Override
    public JForm<?> index(int index) {
        return delegate.index(index);
    }

    @Override
    public Field getField() {
        return delegate.getField();
    }

    @Override
    public Field getField(String path) {
        return delegate.getField(path);
    }

    @Override
    public Messages getMessages() {
        return delegate.getMessages();
    }

    @Override
    public Messages getMessages(String path) {
        return delegate.getMessages(path);
    }

    @Override
    public void info(String msg) {
        delegate.info(msg);
    }

    @Override
    public void info(String path, String msg) {
        delegate.info(path, msg);
    }

    @Override
    public void error(String msg) {
        delegate.error(msg);
    }

    @Override
    public void error(String path, String msg) {
        delegate.error(path, msg);
    }

}
