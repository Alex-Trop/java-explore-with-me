package explore.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@Data
@NoArgsConstructor
public class CommentDto {
    private Integer id;

    @NotNull
    private UserShortDto author;

    @NotNull
    private EventCommentDto event;

    @NotBlank
    private String text;

    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdOn;
}
