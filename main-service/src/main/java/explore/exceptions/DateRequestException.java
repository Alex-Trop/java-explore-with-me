package explore.exceptions;

import lombok.Getter;

@Getter
public class DateRequestException extends RuntimeException {
    private String reason = "Incorrectly made request.";

    public DateRequestException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public DateRequestException(String message) {
        super(message);
    }
}
