package explore.adminapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    private List<Integer> events;

    @NotNull
    private Boolean pinned;

    @NotNull
    @Size(min = 1, max = 50)
    private String title;
}
