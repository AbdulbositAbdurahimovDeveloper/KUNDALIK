package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import uz.kundalik.site.validation.annotations.FieldMatch;

@Data
@FieldMatch(first = ChangePasswordDTO.FIELD_NEW_PASSWORD, second = ChangePasswordDTO.FIELD_CONFIRM_PASSWORD, message = "The new password fields must match.")
public class ChangePasswordDTO {

    public static final String FIELD_NEW_PASSWORD = "newPassword";
    public static final String FIELD_CONFIRM_PASSWORD = "confirmPassword";

    @NotBlank(message = "Current password cannot be blank.")
    private String currentPassword;

    @NotBlank(message = "New password cannot be blank.")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace."
    )
    private String newPassword;

    @NotBlank(message = "Password confirmation is required.")
    private String confirmPassword;
}