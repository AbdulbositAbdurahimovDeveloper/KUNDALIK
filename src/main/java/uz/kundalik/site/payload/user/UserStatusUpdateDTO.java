package uz.kundalik.site.payload.user;

import lombok.Data;

/**
 * A Data Transfer Object used by administrators to update a user's status flags.
 * Using the Boolean wrapper class instead of the primitive boolean allows for partial updates.
 * If a field is null in the request, it will be ignored, and only the non-null fields
 * will be updated.
 */
@Data
public class UserStatusUpdateDTO {

    private Boolean enabled;
    private Boolean accountNonLocked;
}