package samson.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.core.MultivaluedMap;

import samson.JForm;
import samson.bind.Binder;
import samson.bind.BinderNode;
import samson.bind.BinderType;
import samson.convert.Conversion;
import samson.form.Property.Node;
import samson.form.Property.Path;
import samson.metadata.Element;
import samson.metadata.ElementRef;


/**
 * Binding form.
 */
class BindForm<T> extends AbstractForm<T> {

    private static final Logger LOGGER = Logger.getLogger(BindForm.class.getName());

    private static final boolean VALIDATE = true;

    private ParamsProvider formParams;
    private ParamsProvider queryParams;

    private FormNode unnamedRoot;
    private FormNode root;

    private List<Conversion> conversionErrors = Collections.emptyList();

    private Set<ConstraintViolation<T>> violations = Collections.emptySet();

    public BindForm(Element parameter) {
        this(parameter, null);
    }

    public BindForm(Element parameter, T value) {
        super(parameter);
        this.value = value;
    }

    public void setFormParamsProvider(ParamsProvider formParams) {
        this.formParams = formParams;
    }

    public void setQueryParamsProvider(ParamsProvider queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public MultivaluedMap<String, String> getFormParams() {
        return formParams.get();
    }

    @Override
    public MultivaluedMap<String, String> getQueryParams() {
        return queryParams.get();
    }

    @Override
    public JForm<T> apply(MultivaluedMap<String, String> params) {

        buildTree(params);

        bind();

        validate();

        return this;
    }

    private void buildTree(MultivaluedMap<String, String> params) {

        unnamedRoot = new FormNode(Node.createPrefix(null));

        for (String param : params.keySet()) {
            List<String> values = params.get(param);

            Path path = Path.createPath(param);
            if (!path.isEmpty()) {
                FormNode node = unnamedRoot.getDefinedChild(path);
                node.setStringValues(values);
            }
        }

        root = unnamedRoot.getDefinedChild(Path.createPath(rootPath));

        LOGGER.log(Level.FINER, printTree(root));
    }

    private static String printTree(FormNode root) {
        StringBuilder sb = new StringBuilder("\n");
        root.print(sb, 0);
        return sb.toString();
    }

    private void convert(BinderNode binderNode, FormNode formNode) {
        Binder binder = binderNode.getBinder();

        if (binder.getType() == BinderType.STRING) {
            ElementRef ref = binder.getElementRef();

            Conversion conversion = fromStringList(ref.element, binderNode.getStringValues());
            formNode.setConversion(conversion);
            if (conversion.isError()) {
                conversionErrors.add(conversion);
            }
            else {
                ref.accessor.set(conversion.getValue());
            }
        }
        else {
            for (BinderNode binderChild : binderNode.getChildren()) {
                String name = binderChild.getName();
                convert(binderChild, formNode.getChild(name));
            }
        }
    }

    private void bind() {

        ElementRef ref = new ElementRef(parameter, valueAccessor);

        Binder binder = binderFactory.getBinder(ref, root.hasChildren());
        binder.read(root);

        conversionErrors = new ArrayList<Conversion>();
        convert(binder.getNode(), root);

        for (Conversion conversion : conversionErrors) {
            LOGGER.log(Level.FINE, "conversion error cause " + conversion.getCause());
        }

        LOGGER.log(Level.FINER, printTree(root));
    }

    private void validate() {

        if (!VALIDATE || (validatorFactory == null)) {
            return;
        }

        Validator validator = validatorFactory.getValidator();

        if (value != null) {
            violations = validator.validate(value);
        }

        for (ConstraintViolation<T> violation : violations) {
            LOGGER.log(Level.FINE, violation.getPropertyPath() + ": " + violation.getMessage());
        }

        // annotate the parameter tree with violations, parses and normalizes the property path
        for (ConstraintViolation<T> violation : violations) {
            javax.validation.Path path = violation.getPropertyPath();
            String param = path.toString();
            getViolations(param).add(violation);
        }

        LOGGER.log(Level.FINER, printTree(root));
    }

    // -- Form methods

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean hasErrors() {
        if (conversionErrors.size() > 0) {
            return true;
        }
        if (violations.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Conversion> getConversionErrors() {
        return conversionErrors;
    }

    @Override
    public Set<ConstraintViolation<T>> getViolations() {
        return violations;
    }

    // -- Field methods

    private FormNode getFormNode(String param) {
        Path path = Path.createPath(param);
        return root.getDefinedChild(path);
    }

    @Override
    public Object getObjectValue(String param) {
        Conversion binding = getConversion(param);
        if (binding == null) {
            return null;
        }

        return binding.getValue();
    }

    @Override
    public String getValue(String param) {
        FormNode node = getFormNode(param);
        Conversion binding = node.getConversion();
        if (binding == null) {
            return null;
        }

        if (binding.isError()) {
            return Utils.getFirst(node.getStringValues());
        }
        else {
            return toStringValue(binding.getElement(), binding.getValue());
        }
    }

    @Override
    public List<String> getValues(String param) {
        FormNode node = getFormNode(param);
        Conversion binding = node.getConversion();
        if (binding == null) {
            return null;
        }

        if (binding.isError()) {
            return node.getStringValues();
        }
        else {
            return toStringList(binding.getElement(), binding.getValue());
        }
    }

    @Override
    public boolean isError(String param) {
        Conversion binding = getConversion(param);
        if (binding != null) {
            if (binding.isError()) {
                return true;
            }
        }

        if (getViolations(param).size() > 0) {
            return true;
        }

        return false;
    }

    @Override
    public Conversion getConversion(String param) {
        FormNode node = getFormNode(param);
        Conversion binding = node.getConversion();

        return binding;
    }

    @Override
    public String getConversionMessage(String param) {
        FormNode node = getFormNode(param);
        Conversion binding = node.getConversion();
        if (binding == null) {
            return null;
        }

        if (binding.isError()) {
            String stringValue = Utils.getFirst(node.getStringValues());
            return getErrorMessage(binding.getElement(), stringValue);
        }
        return null;
    }

    @Override
    public Set<ConstraintViolation<?>> getViolations(String param) {
        FormNode node = getFormNode(param);
        return node.getViolations();
    }

    @Override
    public List<String> getValidationMessages(String param) {
        List<String> messages = new ArrayList<String>();
        for (ConstraintViolation<?> violation : getViolations(param)) {
            messages.add(violation.getMessage());
        }
        return messages;
    }

    private final static String ERROR_MESSAGE_TEMPLATE = "cannot convert value '%s' to '%s'";

    private static String getErrorMessage(Element element, String value) {
        return String.format(ERROR_MESSAGE_TEMPLATE, value, element.tcp.c.getSimpleName());
    }

}
