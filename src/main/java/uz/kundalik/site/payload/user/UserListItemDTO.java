
package uz.kundalik.site.payload.user;

import lombok.Data;

import java.time.Instant;

/**
 * A Data Transfer Object representing a single user in a list view for administrators.
 * It contains only the essential information needed to identify a user and their status at a glance,
 * making it lightweight and efficient for paginated results. It intentionally omits detailed
 * profile information for performance and clarity.
 */
@Data
public class UserListItemDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String role; // String yuborish API uchun qulayroq
    private boolean enabled;
    private boolean accountNonLocked;
    private Instant createdAt;
}