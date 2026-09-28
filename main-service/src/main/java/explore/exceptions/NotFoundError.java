package explore.exceptions;

import lombok.Getter;

@Getter
public class NotFoundError extends RuntimeException {
    private final String reason = "The required object was not found.";

    public NotFoundError(String message) {
        super(message);
    }
}
