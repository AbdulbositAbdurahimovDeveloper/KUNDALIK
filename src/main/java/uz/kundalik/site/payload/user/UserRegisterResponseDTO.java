package uz.kundalik.site.payload.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.kundalik.site.enums.VerificationMethod;

/**
 * DTO for the response after a successful user registration.
 * It clearly informs the client about the next step required for account activation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterResponseDTO {

    /**
     * The unique identifier of the newly created user.
     */
    private Long userId;

    /**
     * A user-friendly message for the client.
     * Example: "Registration successful. Please check your email to activate your account."
     */
    private String message;

    /**
     * The verification method chosen by the user (EMAIL or PHONE).
     * This helps the frontend to render the appropriate next view (e.g., an "enter SMS code" form).
     */
    private VerificationMethod verificationMethod;

    /**
     * The target address/number where the verification message was sent, partially masked for privacy.
     * Example: "a******t@gmail.com" or "+998...**567".
     */
    private String verificationTarget;
}