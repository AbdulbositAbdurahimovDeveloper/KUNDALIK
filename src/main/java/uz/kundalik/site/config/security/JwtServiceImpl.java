package uz.kundalik.site.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import uz.kundalik.site.model.User;
import uz.kundalik.site.properties.JwtProperties;

import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * The primary implementation of the {@link JwtService} interface.
 * This class handles the creation, parsing, and validation of JSON Web Tokens (JWTs)
 * using the 'jjwt' library. It uses HMAC-SHA256 for signing tokens.
 */
@Component
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;
    private Key signingKey;

    /**
     * Initializes the service by creating the signing key.
     * This method is called once after the bean has been constructed.
     * It decodes the Base64 secret from properties and creates a secure key for signing tokens.
     */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getSecret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * {@inheritDoc}
     * This implementation adds custom claims such as 'roles', 'userId', and 'profileId' to the access token.
     */
    @Override
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // Add user roles to the token claims
        extraClaims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());

        // If the UserDetails is an instance of our custom User class, add more specific claims
        if (userDetails instanceof User user) {
            extraClaims.put("userId", user.getId());
            extraClaims.put("profileId", user.getProfile().getId());
        }

        return buildToken(extraClaims, userDetails, jwtProperties.getAccessTokenExpiration());
    }

    /**
     * {@inheritDoc}
     * The refresh token is generated with no extra claims for security and minimalism.
     */
    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, jwtProperties.getRefreshTokenExpiration());
    }

    /**
     * A generic method to extract a specific claim from a JWT.
     *
     * @param token          The JWT string.
     * @param claimsResolver A function that specifies how to extract the desired claim from the Claims object.
     * @param <T>            The type of the claim to be extracted.
     * @return The extracted claim.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Helper method to build a JWT with the specified claims, subject, and expiration.
     *
     * @param extraClaims The extra claims to include in the token's payload.
     * @param userDetails The user for whom the token is being created (subject).
     * @param expiration  The duration for which the token will be valid.
     * @return The compacted, URL-safe JWT string.
     */
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            Duration expiration
    ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration.toMillis());

        User user = (User) userDetails;

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .claim("userId", user.getId())
                .claim("profileId", user.getProfile().getId())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts all claims from the JWT payload.
     *
     * @param token The JWT string to parse.
     * @return The Claims object containing all data from the token's body.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if a token has expired.
     *
     * @param token The JWT string to check.
     * @return {@code true} if the token's expiration date is before the current date, {@code false} otherwise.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT.
     *
     * @param token The JWT string.
     * @return The expiration {@link Date} of the token.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Checks if a refresh token is still valid (not expired).
     *
     * @param refreshToken The refresh token string.
     * @return {@code true} if the token is valid and not expired, {@code false} otherwise.
     */
    @Override
    public boolean isRefreshTokenValid(String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.after(new Date());
        } catch (Exception ex) {
            // Token noto‘g‘ri formatda yoki imzosi yaroqsiz bo‘lsa
            return false;
        }
    }

}