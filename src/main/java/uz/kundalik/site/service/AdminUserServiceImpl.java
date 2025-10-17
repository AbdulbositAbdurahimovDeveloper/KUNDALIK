package uz.kundalik.site.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.site.enums.Role;
import uz.kundalik.site.exception.AccessDeniedException;
import uz.kundalik.site.exception.BadRequestException;
import uz.kundalik.site.exception.EntityNotFoundException;
import uz.kundalik.site.exception.OperationNotPermittedException;
import uz.kundalik.site.mapper.UserMapper;
import uz.kundalik.site.model.User;
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.site.payload.user.AssignRoleRequestDTO;
import uz.kundalik.site.payload.user.UserListItemDTO;
import uz.kundalik.site.payload.user.UserResponseDTO;
import uz.kundalik.site.payload.user.UserStatusUpdateDTO;
import uz.kundalik.site.repository.UserRepository;
import uz.kundalik.site.service.template.AdminUserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Retrieves a paginated list of all users in the system.
     *
     * @param page The pagination information (page number).
     * @param size The pagination information (size number).
     * @return A {@link Page} of {@link UserListItemDTO} objects.
     */
    @Override
    public PageDTO<UserListItemDTO> getAllUsers(Integer page, Integer size) {

        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<User> users = userRepository.findAll(pageRequest);
        return new PageDTO<>(
                users.stream().map(userMapper::toUserListItemDTO).toList(),
                users
        );
    }

    /**
     * Retrieves the detailed profile of a single user by their ID.
     *
     * @param id The ID of the user to retrieve.
     * @return A DTO containing the user's detailed information.
     * @throws EntityNotFoundException if no user with the given ID exists.
     */
    @Override
    public UserResponseDTO getUserById(Long id) {
        User user = getUser(id);
        return userMapper.toUserResponseDTO(user);
    }

    /**
     * Updates the status of a user's account (e.g., enabled, locked).
     *
     * @param id        The ID of the user to update.
     * @param statusDTO The DTO containing the new status flags.
     * @return The updated user profile DTO.
     * @throws EntityNotFoundException if the user does not exist.
     */
    @Override
    @Transactional
    public UserResponseDTO updateUserStatus(Long id, UserStatusUpdateDTO statusDTO, User currentUser) {
        log.info("Admin {} is attempting to update status for user with ID: {}", currentUser.getUsername(), id);

        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("Admins cannot change their own status.");
        }

        User targetUser = getUser(id);

        if (targetUser.getRole() == Role.SUPER_ADMIN) {
            throw new AccessDeniedException("Cannot modify the status of a SUPER_ADMIN.");
        }

        if (currentUser.getRole() == Role.ADMIN && targetUser.getRole() == Role.ADMIN) {
            throw new AccessDeniedException("Admins cannot modify the status of other Admins.");
        }

        boolean isUpdated = false;
        if (statusDTO.getEnabled() != null && targetUser.isEnabled() != statusDTO.getEnabled()) {
            targetUser.setEnabled(statusDTO.getEnabled());
            isUpdated = true;
            log.info("User {} 'enabled' status changed to {} by admin {}", id, statusDTO.getEnabled(), currentUser.getUsername());
        }

        if (statusDTO.getAccountNonLocked() != null && targetUser.isAccountNonLocked() != statusDTO.getAccountNonLocked()) {
            targetUser.setAccountNonLocked(statusDTO.getAccountNonLocked());
            isUpdated = true;
            log.info("User {} 'accountNonLocked' status changed to {} by admin {}", id, statusDTO.getAccountNonLocked(), currentUser.getUsername());
        }

        if (isUpdated) {
            User savedUser = userRepository.save(targetUser);
            return userMapper.toUserResponseDTO(savedUser);
        }

        log.warn("Update status request for user {} contained no new changes.", id);
        return userMapper.toUserResponseDTO(targetUser);
    }

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
     * @throws EntityNotFoundException        if the user does not exist.
     * @throws OperationNotPermittedException if the acting admin does not have
     *                                        the permission to assign the specified role.
     */
    @Override
    @Transactional
    public UserResponseDTO assignUserRole(Long id, AssignRoleRequestDTO roleDTO, User currentUser) {
        log.info("Admin '{}' is attempting to assign role '{}' to user with ID: {}",
                currentUser.getUsername(), roleDTO.getRole(), id);

        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("You cannot change your own role.");
        }

        User targetUser = getUser(id);

        if (targetUser.getRole() == Role.SUPER_ADMIN) {
            throw new AccessDeniedException("The role of a SUPER_ADMIN cannot be changed.");
        }

        Role newRole = roleDTO.getRole();

        if (currentUser.getRole() != Role.SUPER_ADMIN && (newRole == Role.ADMIN || newRole == Role.SUPER_ADMIN)) {
            throw new AccessDeniedException("You do not have permission to assign ADMIN or SUPER_ADMIN roles.");
        }

        if (targetUser.getRole() == newRole) {
            log.warn("User {} already has the role '{}'. No changes made.", id, newRole);
            return userMapper.toUserResponseDTO(targetUser);
        }

        log.info("Changing role for user {} from {} to {}", id, targetUser.getRole(), newRole);
        targetUser.setRole(newRole);
        User savedUser = userRepository.save(targetUser);

        return userMapper.toUserResponseDTO(savedUser);
    }


    /**
     * Deletes a user account from the system.
     * The implementation can decide whether this is a soft or hard delete.
     *
     * @param id          The ID of the user to delete.
     * @param currentUser The currently authenticated User object, provided by Spring Security's
     *                    {@code @AuthenticationPrincipal}. This ensures the operation is performed
     *                    securely on the correct user's account.
     * @throws EntityNotFoundException if the user does not exist.
     */
    @Override
    @Transactional
    public void deleteUser(Long id, User currentUser) {
        log.info("Admin '{}' is attempting to delete user with ID: {}", currentUser.getUsername(), id);

        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("You cannot delete your own account using this endpoint. Please use the 'Delete My Account' feature in your profile settings.");
        }

        User userToDelete = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        if (userToDelete.getRole() == Role.SUPER_ADMIN) {
            log.warn("SECURITY ALERT: Admin '{}' attempted to delete SUPER_ADMIN account with ID {}.",
                    currentUser.getUsername(), id);
            throw new AccessDeniedException("SUPER_ADMIN accounts cannot be deleted.");
        }

        if (currentUser.getRole() == Role.ADMIN && userToDelete.getRole() == Role.ADMIN) {
            log.warn("SECURITY ALERT: Admin '{}' attempted to delete another Admin account with ID {}.",
                    currentUser.getUsername(), id);
            throw new AccessDeniedException("Admins are not permitted to delete other admin accounts.");
        }

        log.info("Performing soft delete for user {} (ID: {}) by admin '{}'",
                userToDelete.getUsername(), id, currentUser.getUsername());

        userRepository.delete(userToDelete);
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found " + id));
    }
}
