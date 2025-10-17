package uz.kundalik.site.service.generate;

import uz.kundalik.site.enums.TokenType;

public interface GenerateUniqueService {

    /**
     * Generates a 6-digit numeric token and ensures it's not present in DB for the given token type.
     * Retries up to MAX_TOKEN_GENERATION_ATTEMPTS times if a collision is found.
     *
     * @param tokenType TokenType to check uniqueness against (e.g., ACCOUNT_ACTIVATION)
     * @return unique 6-digit token as String
     * @throws IllegalStateException if unable to generate a unique token after many attempts
     */
    String generateUniqueSixDigitToken(TokenType tokenType);
}
