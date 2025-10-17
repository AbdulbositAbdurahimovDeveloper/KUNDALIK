package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;

import java.sql.Timestamp;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reminders")
@SQLDelete(sql = "UPDATE reminders SET deleted = true WHERE id = ?")
public class Reminder extends AbsAuditingSoftDeleteEntity {

    @Column(name = "reminder_type", nullable = false)
    private String reminderType; // Masalan: 'TEXT_MESSAGE', 'WEATHER_FORECAST'

    @Column(columnDefinition = "TEXT")
    private String message;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "action_metadata", columnDefinition = "jsonb")
    private Map<String, Object> actionMetadata;

    private Timestamp remindAt;

    private String recurrenceRule; // Cron expression uchun

    @Column(nullable = false)
    private String status; // Masalan: 'ACTIVE', 'SENT', 'DISABLED'

    // --- Bog'lanishlar ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}