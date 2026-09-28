package explore.privateapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {
    private String created;

    private Integer event;

    private Integer id;

    private Integer requester;

    private String status;
}
