package explore.adminapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NewUserRequest {
    @NotNull
    @Size(min = 6, max = 254)
    @NotBlank
    @Email
    private String email;

    @NotNull
    @Size(min = 2, max = 250)
    @NotBlank
    private String name;
}
