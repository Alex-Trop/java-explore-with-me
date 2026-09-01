package exceptions;

public class ResourceAlreadyExistsError extends RuntimeException {
    public ResourceAlreadyExistsError(String message) {
        super(message);
    }
}
