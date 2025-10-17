package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;
import uz.kundalik.site.enums.TransactionType; // Enum'ni yaratish kerak

import java.math.BigDecimal;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
@SQLDelete(sql = "UPDATE transactions SET deleted = true WHERE id = ?")
public class Transaction extends AbsAuditingSoftDeleteEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR) // <-- YECHIM: Eng ishonchli usul
    private TransactionType transactionType;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "transaction_date", nullable = false)
    private Timestamp transactionDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    // --- Bog'lanishlar ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id")
    private Account fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id")
    private Account toAccount;
}