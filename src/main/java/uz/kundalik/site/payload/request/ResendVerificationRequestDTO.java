package uz.kundalik.site.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResendVerificationRequestDTO {

    @NotBlank(message = "Email kiritilishi shart")
    private String email;
}