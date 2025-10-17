package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import uz.kundalik.site.model.Abs.AbsLongEntity;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_settings")
public class UserSettings extends AbsLongEntity {
    // Bu entity uchun soft delete va createdAt shart emas deb hisobladim.
    // U user bilan birga yaratiladi va faqat o'zgaradi.

    @UpdateTimestamp
    private Timestamp updatedAt;

    // --- Bog'lanishlar ---
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // Optional OneToOne uchun ManyToOne ishlatish qulayroq
    @JoinColumn(name = "default_address_id")
    private Address defaultAddress;
}