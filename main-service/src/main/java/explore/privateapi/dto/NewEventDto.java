package explore.privateapi.dto;

import explore.dtos.LocationDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Integer category;

    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    private String eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid;

    private int participantLimit;

    private Boolean requestModeration;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;
}
