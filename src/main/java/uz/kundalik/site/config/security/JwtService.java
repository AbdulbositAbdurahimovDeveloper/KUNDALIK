package uz.kundalik.site.config.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface for JSON Web Token (JWT) service.
 * Defines the contract for operations such as generating, validating, and extracting information from JWTs.
 */
public interface JwtService {

    /**
     * Extracts the username (subject) from a given JWT.
     *
     * @param jwt The JWT string from which to extract the username.
     * @return The username contained within the token.
     * @throws io.jsonwebtoken.JwtException if the token is malformed or cannot be parsed.
     */
    String extractUsername(String jwt);

    /**
     * Validates a JWT against a UserDetails object.
     * The token is considered valid if the username matches and the token has not expired.
     *
     * @param jwt         The JWT string to validate.
     * @param userDetails The UserDetails object representing the user to validate against.
     * @return {@code true} if the token is valid for the given user, {@code false} otherwise.
     */
    boolean isTokenValid(String jwt, UserDetails userDetails);

    /**
     * Generates a new access token for the specified user.
     * The access token typically has a shorter lifespan and contains user claims like roles.
     *
     * @param userDetails The UserDetails object for whom the token is to be generated.
     * @return A JWT string representing the access token.
     */
    String generateAccessToken(UserDetails userDetails);

    /**
     * Generates a new refresh token for the specified user.
     * The refresh token has a longer lifespan and is used to obtain a new access token.
     *
     * @param userDetails The UserDetails object for whom the token is to be generated.
     * @return A JWT string representing the refresh token.
     */
    String generateRefreshToken(UserDetails userDetails);

    boolean isRefreshTokenValid(String refreshToken);
}