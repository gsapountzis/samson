package samson.form;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderFactory;
import samson.metadata.ElementRef;
import samson.parse.Property.Node;
import samson.parse.Property.Path;
import samson.utils.Utils;

class Form<T> implements JForm<T> {

    private final FormFactory factory;

    private final T value;
    private final ElementRef ref;
    private final FormNode node;
    private final FormField field;

    Form(FormFactory factory, T value, ElementRef ref, FormNode node) {
        this.factory = factory;

        this.value = value;
        this.ref = ref;
        this.node = node;
        this.field = new FormField(factory, ref, node);
    }

    // -- Path

    @Override
    public String getPath() {
        return node.getName();
    }

    @Override
    public JForm<?> path(String path) {
        return new PathForm(factory, this, path);
    }

    @Override
    public JForm<?> dot(String property) {
        String path = getPath();
        if (Utils.isNullOrEmpty(path)) {
            // do nothing
        }
        else {
            property = path + "." + property;
        }
        return new PathForm(factory, this, property);
    }

    @Override
    public JForm<?> index(String index) {
        String path = getPath();
        if (Utils.isNullOrEmpty(path)) {
            // do nothing
        }
        else {
            index = path + "[" + index + "]";
        }
        return new PathForm(factory, this, index);
    }

    @Override
    public JForm<?> index(int index) {
        return index(Integer.toString(index));
    }

    private Path child(Path path) {
        String rootPath = getPath();
        if (Utils.isNullOrEmpty(rootPath)) {
            return path;
        }
        else {
            return path.subpath(1);
        }
    }

    ElementRef getChildRef(Path path) {
        BinderFactory binderFactory = factory.getBinderFactory();

        ElementRef ref = this.ref;
        for (Node node : child(path)) {
            String name = node.getName();
            Binder binder = binderFactory.getBinder(ref, true, false);
            ref = binder.getChildRef(name);
        }
        return ref;
    }

    FormNode getChildNode(Path path) {
        return node.getDefinedChild(child(path));
    }

    // -- Form

    @Override
    public T get() {
        return value;
    }

    @Override
    public boolean hasErrors() {
        return node.isTreeError();
    }

    @Override
    public Map<String, List<String>> getInfos() {
        return getInfos(getPath(), node);
    }

    @Override
    public Map<String, List<String>> getErrors() {
        return getErrors(getPath(), node);
    }

    static Map<String, List<String>> getInfos(String param, FormNode node) {
        Map<String, List<String>> infos = new TreeMap<String, List<String>>();
        node.getTreeInfos(param, infos);
        return infos;
    }

    static Map<String, List<String>> getErrors(String param, FormNode node) {
        Map<String, List<String>> errors = new TreeMap<String, List<String>>();
        node.getTreeErrors(param, errors);
        return errors;
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
