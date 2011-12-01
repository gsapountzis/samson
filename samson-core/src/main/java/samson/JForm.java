package samson;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;

import samson.convert.Conversion;

/**
 * A thin facade over the binding and validation results that is compatible with
 * the JAX-RS programming model.
 * <p>
 * It can be used in the parameter list of a resource method:
 * <pre>
 * &#064;Path(&quot;{id}&quot;)
 * public void post(&#64;PathParam(&quot;id&quot;) Long id, &#064;FormParam(&quot;user&quot;) JForm&lt;User&gt; userForm) {
 * }
 * </pre>
 * Or within the resource method body:
 * <pre>
 * &#64;Context JFormProvider jForm;
 *
 * &#064;Path(&quot;{id}&quot;)
 * public void post(&#64;PathParam(&quot;id&quot;) Long id) {
 *
 *     JForm&lt;User&gt; userForm = jForm.bind(User.class).form(&quot;user&quot;);
 * }
 * </pre>
 * </p>
 *
 * @author sap
 *
 * @param <T>
 *            form object type: a primitive, string or string-based type as
 *            defined by the JAX-RS spec or a composite type (bean, list, map).
 *
 * @see QueryParam
 * @see FormParam
 */
public interface JForm<T> {

    /*
     * Apply methods for binding forms.
     */

    JForm<T> params(MultivaluedMap<String, String> params);

    JForm<T> params(String path, MultivaluedMap<String, String> params);

    JForm<T> form();

    JForm<T> form(String path);

    JForm<T> query();

    JForm<T> query(String path);

    /*
     * Form methods (over the whole tree).
     */

    /** Short for {@link #getValue()} */
    T get();

    T getValue();

    boolean hasErrors();

    List<Conversion> getConversionErrors();

    Set<ConstraintViolation<T>> getViolations();

    /*
     * Field methods (root object is the null or empty string).
     */

    Object getObjectValue(String param);

    String getValue(String param);

    List<String> getValues(String param);

    boolean isError(String param);

    Conversion getConversion(String param);

    String getConversionMessage(String param);

    Set<ConstraintViolation<?>> getViolations(String param);

    List<String> getValidationMessages(String param);

    /**
     * Map of fields abstraction over the form.
     */
    Map<String, Field> getFields();

    /**
     * Form field abstraction.
     * <p>
     * Used for providing a Map of Fields abstraction over the Form for easier
     * access from view templates.
     * </p>
     */
    public static interface Field {

        String getName();

        Object getObjectValue();

        String getValue();

        List<String> getValues();

        boolean isError();

        Conversion getConversion();

        String getConversionMessage();

        Set<ConstraintViolation<?>> getViolations();

        List<String> getValidationMessages();

    }

}
