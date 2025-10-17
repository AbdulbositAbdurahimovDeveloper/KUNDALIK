package uz.kundalik.site.service.template;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.kundalik.site.model.User;
import uz.kundalik.site.payload.response.MessageResponseDTO;
import uz.kundalik.site.payload.user.*;

/**
 * Service interface for managing the profile and security settings
 * of the currently authenticated user.
 */
@Service
public interface UserService extends UserDetailsService {

    /**
     * Retrieves the complete profile information for the currently authenticated user.
     *
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A DTO containing the user's detailed information.
     * @throws uz.kundalik.site.exception.EntityNotFoundException if the authenticated user cannot be found.
     */
    UserResponseDTO getCurrentUserProfile(User currentUser);

    /**
     * Updates the profile information (e.g., first name, last name) of the currently authenticated user.
     * This method supports partial updates; null fields in the request DTO will be ignored.
     *
     * @param requestDTO  The DTO with the fields to update.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return The updated user profile DTO.
     */
    UserResponseDTO updateCurrentUserProfile(UserUpdateRequestDTO requestDTO, User currentUser);

    /**
     * Uploads or updates the profile picture for the currently authenticated user.
     *
     * @param file        The image file to be uploaded.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A success message, possibly including the URL of the new picture.
     * @throws uz.kundalik.site.exception.FileUploadException if the file upload fails.
     */
    MessageResponseDTO uploadProfilePicture(MultipartFile file, User currentUser);

    /**
     * Changes the password for the currently authenticated user after verifying their current password.
     *
     * @param requestDTO  The DTO containing the current and new passwords.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A success message.
     * @throws uz.kundalik.site.exception.InvalidPasswordException if the current password does not match.
     */
    MessageResponseDTO changePassword(ChangePasswordDTO requestDTO, User currentUser);

    /**
     * Initiates the process to change or add the email for the currently authenticated user.
     * It sends a confirmation link to the proposed new email address.
     *
     * @param requestDTO  The DTO containing the new email and current password for verification.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A message indicating that a confirmation link has been sent.
     * @throws uz.kundalik.site.exception.InvalidPasswordException    if the current password is incorrect.
     * @throws uz.kundalik.site.exception.EmailAlreadyExistsException if the new email is already in use.
     */
    MessageResponseDTO requestEmailChange(EmailChangeRequestDTO requestDTO, User currentUser);

    /**
     * Resends the email verification link if the user's email is currently unverified.
     *
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account. The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return A success message indicating the email has been resent.
     * @throws uz.kundalik.site.exception.DataConflictException if the user does not have an email or if it's already verified.
     */
    MessageResponseDTO resendEmailVerification(User currentUser);


    /**
     * Deactivates (soft-deletes) the account of the currently authenticated user.
     *
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     */
    void deleteMyAccount(User currentUser);
}