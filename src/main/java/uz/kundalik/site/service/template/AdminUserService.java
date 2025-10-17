package uz.kundalik.site.service.template;

import org.springframework.data.domain.Page;
import uz.kundalik.site.model.User;
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.site.payload.user.AssignRoleRequestDTO;
import uz.kundalik.site.payload.user.UserListItemDTO;
import uz.kundalik.site.payload.user.UserResponseDTO;
import uz.kundalik.site.payload.user.UserStatusUpdateDTO;

/**
 * Service interface for administrative operations on user accounts.
 * Provides functionalities for viewing, modifying, and deleting users.
 * All methods in this service must be protected by role-based security checks.
 */
public interface AdminUserService {

    /**
     * Retrieves a paginated list of all users in the system.
     *
     * @param page The pagination information (page number).
     * @param size The pagination information (size number).
     * @return A {@link Page} of {@link UserListItemDTO} objects.
     */
    PageDTO<UserListItemDTO> getAllUsers(Integer page, Integer size);

    /**
     * Retrieves the detailed profile of a single user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A DTO containing the user's detailed information.
     * @throws uz.kundalik.site.exception.EntityNotFoundException if no user with the given ID exists.
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Updates the status of a user's account (e.g., enabled, locked).
     *
     * @param id          The ID of the user to update.
     * @param statusDTO   The DTO containing the new status flags.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return The updated user profile DTO.
     * @throws uz.kundalik.site.exception.EntityNotFoundException if the user does not exist.
     */
    UserResponseDTO updateUserStatus(Long id, UserStatusUpdateDTO statusDTO, User currentUser);

    /**
     * Assigns a new role to a user.
     * Implementations must include security logic to prevent privilege escalation
     * (e.g., an ADMIN cannot assign the SUPER_ADMIN role).
     *
     * @param id          The ID of the user whose role is to be changed.
     * @param roleDTO     The DTO containing the new role.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @return The updated user profile DTO.
     * @throws uz.kundalik.site.exception.EntityNotFoundException        if the user does not exist.
     * @throws uz.kundalik.site.exception.OperationNotPermittedException if the acting admin does not have
     *                                                               the permission to assign the specified role.
     */
    UserResponseDTO assignUserRole(Long id, AssignRoleRequestDTO roleDTO, User currentUser);

    /**
     * Deletes a user account from the system.
     * The implementation can decide whether this is a soft or hard delete.
     *
     * @param id          The ID of the user to delete.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @throws uz.kundalik.site.exception.EntityNotFoundException if the user does not exist.
     */
    void deleteUser(Long id, User currentUser);
}