package uz.kundalik.site.service.generate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.kundalik.site.enums.TokenType;
import uz.kundalik.site.repository.VerificationTokenRepository;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateUniqueServiceImpl implements GenerateUniqueService {

    private static final int MAX_TOKEN_GENERATION_ATTEMPTS = 10;
    private final VerificationTokenRepository verificationTokenRepository;

    /**
     * Generates a 6-digit numeric token and ensures it's not present in DB for the given token type.
     * Retries up to MAX_TOKEN_GENERATION_ATTEMPTS times if a collision is found.
     *
     * @param tokenType TokenType to check uniqueness against (e.g., ACCOUNT_ACTIVATION)
     * @return unique 6-digit token as String
     * @throws IllegalStateException if unable to generate a unique token after many attempts
     */
    @Override
    public String generateUniqueSixDigitToken(TokenType tokenType) {
        for (int attempt = 0; attempt < MAX_TOKEN_GENERATION_ATTEMPTS; attempt++) {
            String candidate = generateSixDigitCode();

            boolean exists = verificationTokenRepository.existsByTokenAndTokenType(candidate, tokenType);
            if (!exists) {
                return candidate;
            }
            log.warn("Token collision detected (attempt {}): {}. Retrying...", attempt + 1, candidate);
        }

        throw new IllegalStateException("Unable to generate unique verification token after "
                + MAX_TOKEN_GENERATION_ATTEMPTS + " attempts");
    }

    /**
     * Generates a secure-ish 6-digit number as a String (100000..999999).
     */
    private String generateSixDigitCode() {
        int number = ThreadLocalRandom.current().nextInt(100_000, 1_000_000);
        return Integer.toString(number);
    }
}
