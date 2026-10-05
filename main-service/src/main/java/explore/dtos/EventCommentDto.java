package explore.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
public class EventCommentDto {
    private Integer id;

    @NotNull
    private String title;

    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;
}
