package explore.adminapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDto {
    @NotNull
    private Integer id;

    @NotNull
    @Size(min = 6, max = 254)
    private String email;

    @NotNull
    @Size(min = 2, max = 250)
    private String name;
}
