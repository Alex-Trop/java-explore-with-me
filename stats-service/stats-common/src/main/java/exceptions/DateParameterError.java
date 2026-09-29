package exceptions;

public class DateParameterError extends RuntimeException {
    public DateParameterError(String message) {
        super(message);
    }
}
