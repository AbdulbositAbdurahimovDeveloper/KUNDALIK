package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.kundalik.site.model.Abs.AbsDateEntity; // Faqat sana kerak, deleted emas
import java.sql.Timestamp; // Sizning AbsDateEntity'ga mos
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription extends AbsDateEntity { // --- O'zgarish ---

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Timestamp startsAt;

    private Timestamp expiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}