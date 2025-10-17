package uz.kundalik.site.payload.user;

import lombok.Data;
import uz.kundalik.site.enums.Gender;

import java.time.LocalDate;

/**
 * A DTO representing the public-facing profile information of a user.
 * It is designed to be nested within the main UserResponseDTO and contains only
 * personal details, not authentication-related data.
 */
@Data
public class UserProfileResponseDTO {

    private String firstName;
    private String lastName;

    /**
     * A derived field for client convenience, combining first and last names.
     */
    private String fullName;

    private Gender gender;
    private LocalDate birthDate;

    /**
     * The URL of the user's profile picture, transformed from the Attachment entity.
     */
    private String profilePictureUrl;
}