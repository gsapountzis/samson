package samson;

import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * Facade over the binding and validation results that is compatible with the
 * JAX-RS programming model.
 * <p>
 * JForm essentially overlays a graph-of-fields over the object-graph. It can be
 * used in the parameter list of a resource method, or within the resource
 * method body:
 *
 * <pre>
 * // resource method parameter
 *
 * &#064;Path(&quot;{id}&quot;)
 * public void post(@PathParam(&quot;id&quot;) Long id,
 *         &#064;FormParam(&quot;user&quot;) JForm&lt;User&gt; userForm) {
 *
 * }
 *
 * // resource method body
 *
 * &#064;Context JFormProvider jForm;
 *
 * &#064;Path(&quot;{id}&quot;)
 * public void post(@PathParam(&quot;id&quot;) Long id) {
 *
 *     JForm&lt;User&gt; userForm = jForm.bind(User.class).form(&quot;user&quot;);
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
 *
 * @see javax.ws.rs.QueryParam
 * @see javax.ws.rs.FormParam
 */
public interface JForm<T> {

    /**
     * Get the underlying Java object in a type-safe way.
     */
    T get();

    /**
     * Returns <code>true<code> if the form tree has any error.
     */
    boolean hasErrors();

    Set<Throwable> getConversionErrors();

    Set<ConstraintViolation<T>> getConstraintViolations();

    /**
     * Get the root path of the sub-form.
     */
    String getPath();

    /**
     * Get the sub-form rooted at path.
     */
    JForm<?> path(String path);

    JForm<?> dot(String property);

    JForm<?> index(String index);

    JForm<?> index(int index);

    /**
     * Get the field value for the root object of the form.
     */
    Field getField();

    Field getField(String path);

    /**
     * Get the messages for the root object of the form.
     */
    Messages getMessages();

    Messages getMessages(String path);

    void info(String msg);

    void info(String path, String msg);

    void error(String msg);

    void error(String path, String msg);

    /**
     * Form node value.
     * <p>
     * The node can be either a field (leaf node) or a sub-form (tree of nodes).
     * In the case of a tree, the value corresponds to the <em>root</em> object.
     */
    public static interface Field {

        Element getElement();

        Object getObjectValue();

        String getValue();

        List<String> getValues();

        boolean isError();

        Throwable getConversionError();

        Set<ConstraintViolation<?>> getConstraintViolations();

        Messages getMessages();

    }

    /**
     * Form node messages.
     */
    public static interface Messages {

        String getConversionInfo();

        String getConversionError();

        List<String> getValidationInfos();

        List<String> getValidationErrors(); /* ConstraintViolations */

        List<String> getInfos();

        List<String> getErrors();

    }

}
