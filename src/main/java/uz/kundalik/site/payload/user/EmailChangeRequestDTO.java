package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for initiating an email change request.
 * The user must provide their desired new email address and their current password
 * to authorize this sensitive action.
 */
@Data
public class EmailChangeRequestDTO {

    @NotBlank(message = "New email cannot be blank")
    @Email(message = "Invalid email format")
    private String newEmail;

    @NotBlank(message = "Current password is required to confirm your identity")
    private String currentPassword;
}