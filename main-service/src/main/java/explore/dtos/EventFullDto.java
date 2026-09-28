package explore.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import explore.models.Location;
import explore.models.State;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

import static explore.validation.DateTimeFormat.DATE_TIME_PATTERN;

@Data
public class EventFullDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    CategoryDto categoryDto;

    private int confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime createdOn;

    private String description;

    @NotNull
    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime eventDate;

    private Integer id;

    @NotNull
    private UserShortDto initiator;

    @NotNull
    private Location location;

    @NotNull
    private Boolean paid;

    private int participantLimit;

    @JsonFormat(pattern = DATE_TIME_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private State state;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;

    private int views;
}
