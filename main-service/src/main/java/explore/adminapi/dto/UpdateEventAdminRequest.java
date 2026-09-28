package explore.adminapi.dto;

import explore.dtos.LocationDto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateEventAdminRequest {
    private String annotation;

    private Integer category;

    private String description;

    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private StateAction stateAction;

    private String title;
}
