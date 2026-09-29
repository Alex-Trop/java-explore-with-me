package explore.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserShortDto {
    @NotNull
    private Integer id;

    @NotNull
    @Size(min = 2, max = 250)
    private String name;
}
