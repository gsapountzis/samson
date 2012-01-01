package samson.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import samson.JForm;
import samson.metadata.ElementRef;

class Form<T> implements JForm<T> {

    private final FormFactory factory;

    private final T rootValue;
    private final ElementRef rootRef;
    private final FormNode rootNode;
    private final FormField rootField;

    Form(FormFactory factory, T rootValue, ElementRef rootRef, FormNode rootNode) {
        this.factory = factory;
        this.rootValue = rootValue;
        this.rootRef = rootRef;
        this.rootNode = rootNode;
        this.rootField = new FormField(factory, rootRef, rootNode);
    }

    // -- Path

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public JForm<?> path(String path) {
        return new PathForm(factory, this, path);
    }

    @Override
    public JForm<?> dot(String property) {
        return new PathForm(factory, this, property);
    }

    @Override
    public JForm<?> index(String index) {
        return new PathForm(factory, this, "[" + index + "]");
    }

    @Override
    public JForm<?> index(int index) {
        return index(Integer.toString(index));
    }

    // -- Form

    @Override
    public T get() {
        return rootValue;
    }

    ElementRef getRef() {
        return rootRef;
    }

    FormNode getNode() {
        return rootNode;
    }

    @Override
    public boolean hasErrors() {
        return rootNode.isTreeError();
    }

    @Override
    public Map<String, List<String>> getInfos() {
        return getInfos(null, rootNode);
    }

    @Override
    public Map<String, List<String>> getErrors() {
        return getErrors(null, rootNode);
    }

    Map<String, List<String>> getInfos(String param, FormNode node) {
        Map<String, List<String>> infos = new HashMap<String, List<String>>();
        node.getTreeInfos(param, infos);
        return infos;
    }

    Map<String, List<String>> getErrors(String param, FormNode node) {
        Map<String, List<String>> errors = new HashMap<String, List<String>>();
        node.getTreeErrors(param, errors);
        return errors;
    }

    // -- Field

    @Override
    public Field getField() {
        return rootField;
    }

    @Override
    public Messages getMessages() {
        return rootField;
    }

    @Override
    public void info(String msg) {
        rootNode.info(msg);
    }

    @Override
    public void error(String msg) {
        rootNode.error(msg);
    }

}
