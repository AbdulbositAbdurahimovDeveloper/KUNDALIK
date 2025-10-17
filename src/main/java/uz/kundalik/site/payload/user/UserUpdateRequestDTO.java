package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;
import uz.kundalik.site.enums.Gender;

import java.time.LocalDate;

/**
 * DTO for handling updates to a user's profile information.
 * All fields are optional to support partial updates (PATCH semantics).
 * The client only needs to send the fields they wish to change.
 * <p>
 * Sensitive or identity-related fields like email, password, and role are
 * intentionally excluded from this DTO to prevent unauthorized modifications.
 * Such changes should be handled by separate, dedicated, and more secure business processes.
 */
@Data
public class UserUpdateRequestDTO {

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    private Gender gender;

    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
}