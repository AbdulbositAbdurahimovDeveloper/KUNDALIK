package uz.kundalik.site.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.kundalik.site.enums.AttachmentType;
import uz.kundalik.site.model.Attachment;
import uz.kundalik.site.payload.attachment.AttachmentCreatedResponseDTO;
import uz.kundalik.site.payload.attachment.AttachmentDTO;
import uz.kundalik.site.payload.attachment.AttachmentDetailDTO;
import uz.kundalik.site.service.minio.MinioService;

/**
 * The manual implementation of the AttachmentMapper interface.
 * This class is responsible for all logic related to converting Attachment entities to DTOs,
 * including generating dynamic, type-specific URLs (e.g., for MinIO or YouTube).
 */
@Component
@RequiredArgsConstructor
public class AttachmentMapperImpl implements AttachmentMapper {

    private final MinioService minioService;
    private static final String YOUTUBE_EMBED_URL_TEMPLATE = "https://www.youtube.com/embed/%s";

    @Override
    public AttachmentDetailDTO toDetailDTO(Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        return AttachmentDetailDTO.builder()
                .id(attachment.getId())
                .type(attachment.getType())
                .url(generateUrl(attachment))
                .originalFileName(attachment.getOriginalFileName())
                .contentType(attachment.getContentType())
                .size(attachment.getSize())
                .createdAt(attachment.getCreatedAt() != null ? attachment.getCreatedAt().toLocalDateTime() : null)
                .build();
    }

    @Override
    public AttachmentCreatedResponseDTO toCreatedResponseDTO(Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        // Agar bu DTO'da builder bo'lsa, uni ishlatgan ma'qul.
        // Hozirgi holatda konstruktor ham yaxshi ishlaydi.
        return new AttachmentCreatedResponseDTO(
                attachment.getId(),
                attachment.getType(),
                attachment.getOriginalFileName()
        );
    }

    @Override
    public AttachmentDTO toDTO(Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        return AttachmentDTO.builder()
                .id(attachment.getId())
                .type(attachment.getType())
                .url(generateUrl(attachment))
                .build();
    }

    /**
     * A private helper method to generate the appropriate client-facing URL
     * based on the attachment's type.
     *
     * @param attachment The attachment for which to generate a URL.
     * @return A string containing the resolved URL, or null if it cannot be generated.
     */
    private String generateUrl(Attachment attachment) {
//        if (attachment == null || attachment.getType() == null) {
//            return null;
//        }
//
//        if (attachment.getType() == AttachmentType.IMAGE) {
//            if (attachment.getBucketName() != null && attachment.getObjectKey() != null) {
//                return minioService.getPresignedUrl(attachment.getBucketName(), attachment.getObjectKey());
//            }
//        } else if (attachment.getType() == AttachmentType.VIDEO) {
//            if (attachment.getYoutubeVideoId() != null) {
//                return String.format(YOUTUBE_EMBED_URL_TEMPLATE, attachment.getYoutubeVideoId());
//            }
//        }

        return null; // Return null if a URL cannot be generated for any reason
    }
}