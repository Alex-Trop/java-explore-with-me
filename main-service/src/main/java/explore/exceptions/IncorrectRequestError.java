package explore.exceptions;

import lombok.Getter;

@Getter
public class IncorrectRequestError extends RuntimeException {
    private String reason = "For the requested operation the conditions are not met.";

    public IncorrectRequestError(String message) {
        super(message);
    }

    public IncorrectRequestError(String message, String reason) {
        super(message);
        this.reason = reason;
    }
}
