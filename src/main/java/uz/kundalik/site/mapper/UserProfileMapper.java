package uz.kundalik.site.mapper;

import org.mapstruct.*;
import uz.kundalik.site.model.Attachment;
import uz.kundalik.site.model.UserProfile;
import uz.kundalik.site.payload.user.UserProfileResponseDTO;

/**
 * Mapper for converting {@link UserProfile} entities to {@link UserProfileResponseDTO}s.
 * This mapper handles derived fields like {@code fullName} and constructs a URL
 * for the profile picture. It is intended to be used by the main {@link UserMapper}.
 */
@Mapper(componentModel = "spring")
public interface UserProfileMapper {

    @Mapping(source = "profilePicture", target = "profilePictureUrl", qualifiedByName = "attachmentToUrl")
    UserProfileResponseDTO toProfileResponseDTO(UserProfile profile);

    /**
     * Populates the derived 'fullName' field after the initial mapping is complete.
     * This is cleaner than using a complex expression in an @Mapping annotation.
     *
     * @param dto The target DTO after initial mapping.
     */
    @AfterMapping
    default void populateFullName(@MappingTarget UserProfileResponseDTO dto) {
        if (dto.getFirstName() != null && dto.getLastName() != null) {
            dto.setFullName(dto.getFirstName() + " " + dto.getLastName());
        }
    }

    /**
     * A custom mapping function to convert an {@link Attachment} entity into a publicly
     * accessible URL string. Returns null if the attachment is not present.
     *
     * @param attachment The source Attachment entity.
     * @return A string URL like "/api/attachments/{id}" or null.
     */
    @Named("attachmentToUrl")
    default String attachmentToUrl(Attachment attachment) {
        if (attachment == null || attachment.getId() == null) {
            return null;
        }
        // This is a common pattern. The actual URL structure depends on your file serving endpoint.
        return "/api/v1/attachments/" + attachment.getId();
    }
}