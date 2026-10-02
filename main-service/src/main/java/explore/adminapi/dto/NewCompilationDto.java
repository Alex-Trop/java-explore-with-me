package explore.adminapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class NewCompilationDto {
    private List<Integer> events;

    private Boolean pinned;

    @Size(min = 1, max = 50)
    @NotBlank
    private String title;
}
