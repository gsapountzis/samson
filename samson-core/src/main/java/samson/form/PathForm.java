package samson.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.ElementRef;

class PathForm implements JForm<Object> {

    private final Form<?> form;
    private final String param;

    private final FormNode node;
    private final FormField field;

    PathForm(Form<?> form, String param) {
        this.form = form;
        this.param = param;

        Path path = Path.createPath(param);
        if (path.isEmpty()) {
            throw new IllegalArgumentException();
        }

        FormNode rootNode = form.getNode();
        ElementRef rootRef = form.getRef();

        this.node = rootNode.getDefinedChild(path);

        List<ElementRef> refs = getPathRef(rootRef, path);
        int size = refs.size();
        ElementRef ref = refs.get(size - 1);
        ElementRef parentRef = refs.get(size - 2);

        this.field = new FormField(form, node, ref, parentRef.element);
    }

    private List<ElementRef> getPathRef(ElementRef rootRef, Path path) {
        BinderFactory binderFactory = form.getBinderFactory();

        List<ElementRef> refs = new ArrayList<ElementRef>();
        ElementRef ref = rootRef;
        refs.add(ref);
        for (Node node : path) {
            String name = node.getName();
            Binder binder = binderFactory.getBinder(ref, true, false);
            ref = binder.getChildRef(name);
            refs.add(ref);
        }
        return refs;
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
        return new PathForm(form, param + "." + property);
    }

    @Override
    public JForm<?> index(String index) {
        return new PathForm(form, param + "[" + index + "]");
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
