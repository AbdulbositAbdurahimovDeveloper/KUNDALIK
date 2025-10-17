package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for initiating the password reset process.
 * The user provides their email address, and the system will send a password reset link
 * if an account associated with that email exists.
 */
@Data
public class ForgotPasswordRequestDTO {

    @NotBlank(message = "Email kiritilishi shart")
    private String email;
}