package samson;

public class Configuration {

    /**
     * Disable bean validation
     */
    public static final boolean DISABLE_VALIDATION = false;

    /**
     * Maximum list size
     * <p>
     * This is to prevent DoS attacks. For example the attacker could just set
     * the list index to (2<sup>32</sup> - 1) and cause the allocation of more
     * than 4GB of memory.
     */
    public static final int MAX_LIST_SIZE = 256;

    public static final String CONVERSION_ERROR_MESSAGE_TEMPLATE = "invalid value '%s'";

}