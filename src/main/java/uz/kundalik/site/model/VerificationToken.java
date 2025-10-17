package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.kundalik.site.enums.TokenType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "verification_tokens")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "token_type")
    private TokenType tokenType;

    @Column(nullable = false, name = "expiry_date")
    private LocalDateTime expiryDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    // ManyToOne'dan foydalanish OneToOne'dan ko'ra moslashuvchanroq
    // Masalan, bir userda ham akkauntni aktivlash, ham parolni tiklash tokeni bir vaqtda bo'lishi mumkin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;
}