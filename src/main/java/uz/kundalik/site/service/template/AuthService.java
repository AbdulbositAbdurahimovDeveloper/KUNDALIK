package uz.kundalik.site.service.template;

import uz.kundalik.site.payload.request.LoginDTO;
import uz.kundalik.site.payload.request.RefreshTokenRequestDTO;
import uz.kundalik.site.payload.request.ResendVerificationRequestDTO;
import uz.kundalik.site.payload.request.ResetPasswordRequestDTO;
import uz.kundalik.site.payload.response.MessageResponseDTO;
import uz.kundalik.site.payload.response.TokenDTO;
import uz.kundalik.site.payload.user.ForgotPasswordRequestDTO;
import uz.kundalik.site.payload.user.UserRegisterRequestDTO;
import uz.kundalik.site.payload.user.UserRegisterResponseDTO;
import uz.kundalik.site.payload.user.VerifyCodeRequestDTO;

/**
 * Service interface for handling authentication-related operations.
 * This includes user registration, email verification, login, token management,
 * and password reset flows.
 */
public interface AuthService {

    /**
     * Registers a new user with the {@code USER} role.
     * The user is created in an inactive state (enabled=false) pending email verification.
     * An email with a verification link is sent to the user's provided email address.
     *
     * @param requestDTO The DTO containing registration details (email, password, profile info).
     * @return A DTO with the new user's ID and a message indicating that verification is required.
     * @throws uz.kundalik.site.exception.EmailAlreadyExistsException if the email is already in use.
     */
    UserRegisterResponseDTO registerUser(UserRegisterRequestDTO requestDTO);

    /**
     * Verifies a 6-digit code sent to the user's contact information (email or phone).
     * <p>
     * This method is used during:
     * <ul>
     *   <li>User registration — to activate a newly created account.</li>
     *   <li>Account recovery flows — such as confirming phone/email changes (if extended later).</li>
     * </ul>
     * <p>
     * The method validates the code against the database and, if valid and not expired:
     * <ul>
     *   <li>Marks the user as {@code enabled=true} and {@code emailVerified=true} (for email verification).</li>
     *   <li>Deletes or invalidates the verification token to prevent reuse.</li>
     * </ul>
     *
     * @param requestDTO A DTO containing the user's contact (email or phone) and the 6-digit code.
     * @return A message response indicating whether the verification was successful or failed.
     *
     * @throws uz.kundalik.site.exception.InvalidTokenException if the code is invalid or expired.
     * @throws uz.kundalik.site.exception.EntityNotFoundException if the user associated with the contact cannot be found.
     */
    MessageResponseDTO verifyUserCode(VerifyCodeRequestDTO requestDTO);


    /**
     * Authenticates a user based on their credentials.
     * If authentication is successful, it generates and returns JWT access and refresh tokens.
     *
     * @param loginDTO The DTO containing the user's email and password.
     * @return A DTO containing the access and refresh tokens.
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid.
     */
    TokenDTO loginUser(LoginDTO loginDTO);

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * @param requestDTO The DTO containing the refresh token.
     * @return A new DTO with a fresh access token and the original refresh token.
     * @throws uz.kundalik.site.exception.InvalidTokenException if the refresh token is invalid or expired.
     */
    TokenDTO refreshToken(RefreshTokenRequestDTO requestDTO);

    /**
     * Initiates the password reset process for a user.
     * It generates a password reset token and sends a reset link to the user's email.
     * The response is always generic to prevent user enumeration attacks.
     *
     * @param requestDTO The DTO containing the user's email.
     * @return A generic success message.
     */
    MessageResponseDTO initiatePasswordReset(ForgotPasswordRequestDTO requestDTO);

    /**
     * Completes the password reset process by setting a new password for the user.
     *
     * @param token The password reset token from the email link.
     * @param requestDTO The DTO containing the new password and its confirmation.
     * @return A success message upon completion.
     * @throws uz.kundalik.site.exception.InvalidTokenException if the token is invalid or expired.
     * @throws uz.kundalik.site.exception.PasswordMismatchException if the new password and confirmation do not match.
     */
    MessageResponseDTO completePasswordReset(String token, ResetPasswordRequestDTO requestDTO);

    String processEmailChangeConfirmation(String token, String redirectUrl);

    /**
     * Resends the initial verification message (email or SMS) to a user who has not yet
     * activated their account after registration. This is a public-facing endpoint.
     *
     * @param requestDTO DTO containing the user's email or phone number.
     * @return A success message.
     */
    MessageResponseDTO resendInitialVerification(ResendVerificationRequestDTO requestDTO);
}