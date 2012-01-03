package samson;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;

import samson.convert.ConverterException;
import samson.metadata.Element;

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
 *     JForm&lt;User&gt; userForm = jForm.form(&quot;user&quot;).bind(User.class);
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
     * Get the root path of the form.
     */
    String getPath();

    /**
     * Get the form rooted at path.
     */
    JForm<?> path(String path);

    /**
     * Get the form for a child property.
     */
    JForm<?> dot(String property);

    /**
     * Get the form for a child index.
     */
    JForm<?> index(String index);

    /**
     * Get the form for a child index.
     */
    JForm<?> index(int index);

    /**
     * Get the underlying object in a type-safe way.
     */
    T get();

    /**
     * Returns <code>true<code> if the form has any error.
     */
    boolean hasErrors();

    /**
     * Convenient method for getting all the info messages of the form.
     */
    Map<String, List<String>> getInfos();

    /**
     * Convenient method for getting all the error (conversion, validation, user) messages of the form.
     */
    Map<String, List<String>> getErrors();

    /**
     * Get the field value for the root object of the form.
     */
    Field getField();

    /**
     * Get the messages for the root object of the form.
     */
    Messages getMessages();

    /**
     * Add an info message for the root object of the form.
     */
    void info(String msg);

    /**
     * Add an error message for the root object of the form.
     */
    void error(String msg);

    /**
     * Form node value.
     * <p>
     * The node can be either a field (leaf node) or a form (tree of nodes). In
     * the case of a tree, the value corresponds to the <em>root</em> object.
     */
    public static interface Field {

        Element getElement();

        Object getObjectValue();

        String getValue();

        List<String> getValues();

        boolean isError();

        ConverterException getConversionFailure();

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
