package samson.form;

/**
 * Facade over the binding and validation results that is compatible with the
 * JAX-RS programming model.
 * <p>
 * {@link SamsonForm} essentially overlays a graph-of-nodes over the
 * object-graph. It can be used in the parameter list of a resource method, or
 * within the resource method body:
 *
 * <pre>
 * // resource method parameter
 *
 * &#064;Path(&quot;{id}&quot;)
 * public void post(@PathParam(&quot;id&quot;) Long id, @FormParam(&quot;user&quot;) SamsonForm&lt;User&gt; userForm) {
 *
 * }
 *
 * // resource method body
 *
 * &#064;Context
 * FormProvider formProvider;
 *
 * &#064;Path(&quot;{id}&quot;)
 * public void post(@PathParam(&quot;id&quot;) Long id) {
 *
 *     SamsonForm&lt;User&gt; userForm = formProvider.form(&quot;user&quot;).bind(User.class);
 * }
 * </pre>
 *
 * </p>
 *
 * @author sap
 *
 * @param <T>
 *            form object type: a primitive, string or string-based type as
 *            defined by the JAX-RS spec or a composite type (bean, list, map).
 */
public class SamsonForm<T> {

    private final T value;
    private final FormNode node;

    public SamsonForm(T value, FormNode node) {
        this.value = value;
        this.node = node;
    }

    /**
     * Get the underlying object in a type-safe way.
     */
    public T get() {
        return value;
    }

    /**
     * Get the node for the root object of the form.
     */
    public FormNode node() {
        return node;
    }

    /**
     * Returns <code>true<code> if the form has any error.
     */
    public boolean hasErrors() {
        return node.isTreeError();
    }

}
