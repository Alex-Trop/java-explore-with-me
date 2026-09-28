package explore.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@Data
public class EventShortDto {
    @NotNull
    private String annotation;

    @NotNull
    private CategoryDto categoryDto;

    private int confirmedRequests;

    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    private Integer id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotNull
    private String title;

    private int views;
}
