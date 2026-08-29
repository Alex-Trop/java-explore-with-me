package dto.hits;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import static exceptions.ErrorDetails.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    @NotNull(message = APP_ERROR)
    private String app;

    @NotNull(message = URI_ERROR)
    private String uri;

    @NotNull(message = IP_ERROR)
    private String ip;

    @NotNull(message = TIME_ERROR)
    @DateTimeFormat
    private String timestamp;
}
