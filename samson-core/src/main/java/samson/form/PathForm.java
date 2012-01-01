package samson.form;

import java.util.List;
import java.util.Map;

import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.ElementRef;

class PathForm implements JForm<Object> {

    private final FormFactory factory;

    private final Form<?> form;
    private final String param;

    private final FormNode node;
    private final FormField field;

    PathForm(FormFactory factory, Form<?> form, String param) {
        this.factory = factory;
        this.form = form;
        this.param = param;

        Path path = Path.createPath(param);
        if (path.isEmpty()) {
            throw new IllegalArgumentException();
        }

        ElementRef rootRef = form.getRef();
        FormNode rootNode = form.getNode();

        ElementRef ref = getPathRef(rootRef, path);
        this.node = rootNode.getDefinedChild(path);
        this.field = new FormField(factory, ref, node);
    }

    private ElementRef getPathRef(ElementRef rootRef, Path path) {
        BinderFactory binderFactory = factory.getBinderFactory();

        ElementRef ref = rootRef;
        for (Node node : path) {
            String name = node.getName();
            Binder binder = binderFactory.getBinder(ref, true, false);
            ref = binder.getChildRef(name);
        }
        return ref;
    }

    // -- Path

    @Override
    public String getPath() {
        return param;
    }

    @Override
    public JForm<?> path(String path) {
        return form.path(path);
    }

    @Override
    public JForm<?> dot(String property) {
        return new PathForm(factory, form, param + "." + property);
    }

    @Override
    public JForm<?> index(String index) {
        return new PathForm(factory, form, param + "[" + index + "]");
    }

    @Override
    public JForm<?> index(int index) {
        return index(Integer.toString(index));
    }

    // -- Form

    @Override
    public Object get() {
        return field.getObjectValue();
    }

    @Override
    public boolean hasErrors() {
        return node.isTreeError();
    }

    @Override
    public Map<String, List<String>> getInfos() {
        return form.getInfos(param, node);
    }

    @Override
    public Map<String, List<String>> getErrors() {
        return form.getErrors(param, node);
    }

    // -- Field

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public Messages getMessages() {
        return field;
    }

    @Override
    public void info(String msg) {
        node.info(msg);
    }

    @Override
    public void error(String msg) {
        node.error(msg);
    }

}
