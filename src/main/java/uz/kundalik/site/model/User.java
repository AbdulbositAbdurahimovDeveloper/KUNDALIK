package uz.kundalik.site.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import uz.kundalik.site.enums.Role;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;
import uz.kundalik.telegram.model.TelegramUser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Represents a user in the system, adapted for multi-identifier authentication (email or phone number).
 * <p>
 * This entity implements {@link UserDetails} for Spring Security integration.
 * A user MUST have either an email or a phone number to register. This is enforced at the database
 * level with a CHECK constraint (e.g., CHECK (email IS NOT NULL OR phone_number IS NOT NULL)).
 * Soft deletion is enabled.
 */
@Getter
@Setter
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "phone_number")
})
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
@JsonIgnoreProperties({
        "authorities",
        "accountNonExpired",
        "credentialsNonExpired"
})
public class User extends AbsAuditingSoftDeleteEntity implements UserDetails {

    /**
     * The unique email address of the user. Used for communication and login. Can be null if phoneNumber is provided.
     */
    @Column(name = "email") // unique is handled by @Table annotation
    private String email;

    /**
     * The hashed password for the user. Cannot be null.
     */

    @Column(nullable = false)
    private String password;

    /**
     * The role of the user, which determines their permissions in the system.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    /**
     * A flag to indicate if the user's account is locked. A locked account cannot be authenticated.
     */
    private boolean accountNonLocked = true;

    /**
     * A flag to indicate if the user's account is enabled. A disabled account cannot be authenticated.
     * Typically set to true after the user verifies their primary identifier (email or phone).
     */
    private boolean enabled = false;

    /**
     * The user's profile, containing personal information like first name and last name.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscription> subscriptions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> address;

    /**
     * The Telegram profile associated with this site user.
     */
    @OneToOne(mappedBy = "siteUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("user-telegram")
    private TelegramUser telegramUser;

    @Column(name = "verification_code_request_count", nullable = false)
    private int verificationCodeRequestCount = 0;

    @Column(name = "last_verification_code_request_at")
    private LocalDateTime lastVerificationCodeRequestAt;


    // --- Helper methods for relationship management ---

    public void linkProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }

    // --- UserDetails Interface Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    /**
     * Returns the primary identifier for authentication.
     * Spring Security requires a non-null username. This method returns the email if available,
     * otherwise the phone number. The database constraint ensures at least one is non-null.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}