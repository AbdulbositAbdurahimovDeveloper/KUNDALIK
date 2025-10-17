package uz.kundalik.site.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import uz.kundalik.site.enums.Gender;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;

import java.time.LocalDate;

/**
 * Stores additional personal information for a {@link User}.
 * <p>
 * This entity has a strict one-to-one relationship with the {@link User} entity and is the owning side of the relationship.
 * The lifecycle of a UserProfile is tied to its parent User.
 * It also supports soft deletion.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"user", "profilePicture"})
@Entity
@Table(name = "user_profiles")
@SQLDelete(sql = "UPDATE user_profiles SET deleted = true WHERE id = ?")
public class UserProfile extends AbsAuditingSoftDeleteEntity {

    /**
     * The user's first name.
     */
    @Column(nullable = false)
    private String firstName;

    /**
     * The user's last name.
     */
    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthDate;

    private String phoneNumber;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Attachment profilePicture;

    /**
     * The parent {@link User} to whom this profile belongs.
     * This is the owning side of the relationship. The {@code user_id} foreign key cannot be null.
     * FetchType is LAZY to prevent unnecessary joins when loading a UserProfile.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

}