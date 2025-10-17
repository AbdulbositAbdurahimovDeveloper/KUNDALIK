package uz.kundalik.site.payload.user;

import lombok.Data;

import java.time.Instant;

/**
 * A comprehensive DTO for sending user data to the client, updated for the new authentication architecture.
 * It securely exposes necessary account and profile information, including both email and phone identifiers,
 * while hiding sensitive or internal fields like password.
 */
@Data
public class UserResponseDTO {

    private Long id;

    /**
     * The user's email address. Can be null if the user registered with a phone number.
     */
    private String email;

    /**
     * The user's phone number. Can be null if the user registered with an email.
     */
    private String phoneNumber;

    private String role;
    private boolean enabled;

    /**
     * A flag indicating if the user's email address has been verified.
     */
    private boolean emailVerified;

    /**
     * A flag indicating if the user's phone number has been verified.
     */
    private boolean phoneVerified;

    private Instant createdAt;

    /**
     * Nested DTO containing the user's personal profile information.
     * This keeps the data structure organized and clean.
     */
    private UserProfileResponseDTO profile;

    /**
     * A simple flag indicating if a Telegram account is linked.
     * This avoids exposing the entire TelegramUser entity.
     */
    private boolean telegramConnected;
}