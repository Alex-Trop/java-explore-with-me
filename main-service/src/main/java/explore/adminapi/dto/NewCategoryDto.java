package explore.adminapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewCategoryDto {
    @NotNull
    @Size(min = 1, max = 50)
    @NotBlank
    private String name;
}
