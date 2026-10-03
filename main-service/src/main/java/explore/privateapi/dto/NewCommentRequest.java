package explore.privateapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewCommentRequest {
    @NotBlank
    @Size(max = 250)
    private String text;
}
