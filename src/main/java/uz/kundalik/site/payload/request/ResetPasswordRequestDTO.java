package uz.kundalik.site.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import uz.kundalik.site.validation.annotations.FieldMatch;

/**
 * Data Transfer Object for handling password reset requests.
 * It includes the reset token, the new password, and a confirmation field.
 * Custom validation ensures that the new password and its confirmation match.
 */
@Data
@FieldMatch(first = "newPassword", second = "confirmPassword", message = "The password fields must match")
public class ResetPasswordRequestDTO {

    @NotBlank(message = "New password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace."
    )
    private String newPassword;

    @NotBlank(message = "Password confirmation cannot be blank")
    private String confirmPassword;
}