package samson.form;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import samson.JForm;
import samson.form.Property.Path;
import samson.metadata.ElementRef;

class PathForm implements JForm<Object> {

    private final FormFactory factory;

    private final Form<?> form;
    private final String param;

    private final ElementRef ref;
    private final FormNode node;
    private final FormField field;

    PathForm(FormFactory factory, Form<?> form, String param) {
        this.factory = factory;
        this.form = form;
        this.param = param;

        try {
            Path path = Path.createPath(param);
            this.ref = form.getChildRef(path);
            this.node = form.getChildNode(path);
            this.field = new FormField(factory, ref, node);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Cannot parse path " + param);
        }
    }

    // -- Path

    @Override
    public String getPath() {
        return param;
    }

    @Override
    public JForm<?> path(String path) {
        return new PathForm(factory, form, path);
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
        return ref.accessor.get();
    }

    @Override
    public boolean hasErrors() {
        return node.isTreeError();
    }

    @Override
    public Map<String, List<String>> getInfos() {
        return Form.getInfos(param, node);
    }

    @Override
    public Map<String, List<String>> getErrors() {
        return Form.getErrors(param, node);
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
