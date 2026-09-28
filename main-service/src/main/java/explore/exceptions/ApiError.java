package explore.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@Data
@AllArgsConstructor
public class ApiError {
    String message;

    String reason;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    final LocalDateTime timestamp = LocalDateTime.now();
}
