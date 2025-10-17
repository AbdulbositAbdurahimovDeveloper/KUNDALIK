package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
@SQLDelete(sql = "UPDATE accounts SET deleted = true WHERE id = ?")
public class Account extends AbsAuditingSoftDeleteEntity { // --- O'zgarish ---

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private String currencyCode;

    @Column(precision = 18, scale = 2)
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}