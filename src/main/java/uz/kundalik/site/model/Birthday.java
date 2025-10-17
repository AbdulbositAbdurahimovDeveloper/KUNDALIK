package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "birthdays")
@SQLDelete(sql = "UPDATE birthdays SET deleted = true WHERE id = ?")
public class Birthday extends AbsAuditingSoftDeleteEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "notify_days_before", nullable = false)
    private int notifyDaysBefore = 3;

    @Column(name = "last_notified_year")
    private Integer lastNotifiedYear;

    // --- Bog'lanishlar ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}