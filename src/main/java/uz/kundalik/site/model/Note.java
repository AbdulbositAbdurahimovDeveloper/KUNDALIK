package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notes")
@SQLDelete(sql = "UPDATE notes SET deleted = true WHERE id = ?")
public class Note extends AbsAuditingSoftDeleteEntity {

    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // --- Bog'lanishlar ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}