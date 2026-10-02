package explore.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CompilationDto {
    private List<EventShortDto> events;

    @NotNull
    private Integer id;

    @NotNull
    private Boolean pinned;

    @NotNull
    private String title;
}
