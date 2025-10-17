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
@Table(name = "message_keys", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"key"})
})
@SQLDelete(sql = "UPDATE users SET deleted = true WHERE id = ?")
public class MessageKey extends AbsAuditingSoftDeleteEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String key; // Masalan: "welcome_message", "weather_not_found"

    @Column(columnDefinition = "TEXT")
    private String description; // Admin yoki dasturchi uchun izoh
}