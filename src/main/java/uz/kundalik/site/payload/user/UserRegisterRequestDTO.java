package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import uz.kundalik.site.enums.Gender;
import uz.kundalik.site.enums.VerificationMethod;
//import uz.kundalik.site.validation.annotations.ValidRegistrationRequest;

import java.time.LocalDate;

/**
 * DTO for new user registration, designed for the new architecture.
 * It uses a custom class-level annotation @ValidRegistrationRequest to ensure that
 * either an email or a phone number is provided and that it matches the selected
 * verification method.
 */
@Data
public class UserRegisterRequestDTO {

    // Can be null if phoneNumber is provided
    @NotNull
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 4, max = 30, message = "Password must be between 4 and 30 characters")
    private String password;

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotNull(message = "Gender must be specified")
    private Gender gender;

    private String initData;

}