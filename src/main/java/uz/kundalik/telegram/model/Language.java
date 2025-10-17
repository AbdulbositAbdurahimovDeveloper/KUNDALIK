package uz.kundalik.telegram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;
import uz.kundalik.site.model.Abs.AbsLongEntity;

@Getter
@Setter
@Entity
@Table(name = "languages", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"})
})
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
public class Language extends AbsAuditingSoftDeleteEntity {

    @Column(nullable = false, unique = true, updatable = false, length = 10)
    private String code; // Masalan: "uz", "en", "ru", "uz_cy"

    @Column(nullable = false, length = 100)
    private String name; // Masalan: "O'zbekcha (Lotin)", "Русский"

    @Column(nullable = false)
    private boolean active = true; // Admin tomonidan tilni yoqish/o'chirish uchun

    @Column(name = "is_default", nullable = false)
    private boolean defaultLanguage = false; // Tizimdagi asosiy tilni belgilash uchun
}