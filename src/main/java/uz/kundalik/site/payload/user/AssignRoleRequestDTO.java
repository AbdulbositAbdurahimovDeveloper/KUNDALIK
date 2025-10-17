package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import uz.kundalik.site.enums.Role;

/**
 * A Data Transfer Object for assigning a new role to a user.
 * It uses the Role enum directly to leverage type safety and ensure that only valid
 * roles can be assigned, preventing invalid data from reaching the service layer.
 */
@Data
public class AssignRoleRequestDTO {

    @NotNull(message = "Role cannot be null.")
    private Role role;
}