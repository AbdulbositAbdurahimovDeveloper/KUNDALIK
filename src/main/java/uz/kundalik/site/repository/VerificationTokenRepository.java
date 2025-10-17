package uz.kundalik.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.kundalik.site.enums.TokenType;
import uz.kundalik.site.model.User;
import uz.kundalik.site.model.VerificationToken;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    /**
     * Finds all tokens of a specific type that expired before the given timestamp.
     * This method is used by the cleanup service to find unverified users.
     *
     * @param now The current timestamp.
     * @param tokenType The specific type of token to find (e.g., ACCOUNT_ACTIVATION).
     * @return A list of expired verification tokens.
     */
    List<VerificationToken> findAllByExpiryDateBeforeAndTokenType(LocalDateTime now, TokenType tokenType);

    boolean existsByTokenAndTokenType(String token, TokenType tokenType);

    Optional<VerificationToken> findByToken(String token);


    Optional<VerificationToken> findByTokenAndTokenType(String token, TokenType tokenType);

    void deleteByUserAndTokenType(User user, TokenType tokenType);

    Optional<VerificationToken> findByTokenAndUserAndTokenType(String token, User user, TokenType tokenType);


    Optional<VerificationToken> findTopByUserAndTokenTypeOrderByExpiryDateDesc(User user, TokenType tokenType);
}