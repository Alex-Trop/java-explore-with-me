package exceptions;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;

    public ErrorResponse(String message) {
        this.error = message;
    }
}
