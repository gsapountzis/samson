package samson.convert;

public class ConverterException extends RuntimeException {

    private static final long serialVersionUID = -8735909941488910999L;

    public ConverterException() {
        super();
    }

    public ConverterException(String message) {
        super(message);
    }

    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConverterException(Throwable cause) {
        super(cause);
    }

}
