package uz.kundalik.site.mapper;

import uz.kundalik.site.model.Attachment;
import uz.kundalik.site.payload.attachment.AttachmentCreatedResponseDTO;
import uz.kundalik.site.payload.attachment.AttachmentDTO;
import uz.kundalik.site.payload.attachment.AttachmentDetailDTO;

/**
 * An interface for manually mapping Attachment entities to their DTOs.
 * This contract separates the mapping logic from its implementation.
 */
public interface AttachmentMapper {

    /**
     * Converts an Attachment entity to a detailed DTO, including a resolved URL.
     *
     * @param attachment The source Attachment entity.
     * @return A detailed DTO with a client-facing URL.
     */
    AttachmentDetailDTO toDetailDTO(Attachment attachment);

    /**
     * Converts an Attachment entity to a simplified DTO returned after creation.
     *
     * @param attachment The source Attachment entity.
     * @return A simplified DTO with the entity's ID and name.
     */
    AttachmentCreatedResponseDTO toCreatedResponseDTO(Attachment attachment);

    AttachmentDTO toDTO(Attachment mainImage);

}