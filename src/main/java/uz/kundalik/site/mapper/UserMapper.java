package uz.kundalik.site.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uz.kundalik.site.enums.Role;
import uz.kundalik.site.model.User;
import uz.kundalik.site.payload.user.SimpleUserDTO;
import uz.kundalik.site.payload.user.UserListItemDTO;
import uz.kundalik.site.payload.user.UserResponseDTO;
import uz.kundalik.telegram.model.TelegramUser;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Main mapper for converting {@link User} entities into various DTOs.
 * This mapper handles complex transformations, such as:
 * - Hiding sensitive data like passwords.
 * - Converting enums to strings.
 * - Mapping nested objects using other mappers (e.g., UserProfileMapper).
 * - Deriving boolean flags from related entities (e.g., isTelegramConnected).
 */
@Mapper(componentModel = "spring", uses = {UserProfileMapper.class})
public interface UserMapper {

    /**
     * Converts a User entity to a lightweight DTO for list views.
     * It flattens the structure by taking firstName and lastName from the nested profile object.
     *
     * @param user The source User entity.
     * @return A UserListItemDTO.
     */
    @Mapping(source = "profile.firstName", target = "firstName") // Nested property'dan olinadi
    @Mapping(source = "profile.lastName", target = "lastName")   // Nested property'dan olinadi
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString") // Enum'ni String'ga o'tkazadi
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "timestampToInstant") // Timestamp'ni Instant'ga o'tkazadi
    UserListItemDTO toUserListItemDTO(User user);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "profile", source = "profile")
    @Mapping(target = "enabled", source = "enabled")
//    @Mapping(target = "emailVerified", source = "emailVerified")
    @Mapping(target = "role", source = "role", qualifiedByName = "roleToString")
    @Mapping(target = "telegramConnected", source = "telegramUser", qualifiedByName = "telegramUserToBoolean")
    @Mapping(source = "createdAt", target = "createdAt", qualifiedByName = "timestampToInstant")
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(source = "profile.firstName", target = "firstName")
    @Mapping(source = "profile.lastName", target = "lastName")
    SimpleUserDTO toSimpleDto(User user);

    /**
     * Converts a Role enum to its string representation.
     *
     * @param role The source Role enum.
     * @return The name of the role as a String (e.g., "ADMIN").
     */
    @Named("roleToString")
    default String roleToString(Role role) {
        return (role == null) ? null : role.name();
    }

    /**
     * Derives a boolean flag indicating if a user's Telegram account is connected.
     *
     * @param telegramUser The associated TelegramUser entity.
     * @return {@code true} if the telegramUser is not null, otherwise {@code false}.
     */
    @Named("telegramUserToBoolean")
    default boolean telegramUserToBoolean(TelegramUser telegramUser) {
        return telegramUser != null;
    }

    /**
     * Converts a java.sql.Timestamp to a java.time.Instant.
     * This is the recommended and most direct way to handle this conversion.
     *
     * @param timestamp The source Timestamp object.
     * @return The corresponding Instant object, or null if the input is null.
     */
    @Named("timestampToInstant")
    default Instant timestampToInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant();
    }
}