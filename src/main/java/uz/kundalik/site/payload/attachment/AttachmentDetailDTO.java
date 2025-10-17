package uz.kundalik.site.payload.attachment;

import lombok.Builder;
import lombok.Data;
import uz.kundalik.site.enums.AttachmentType;

import java.time.LocalDateTime;

/**
 * A detailed DTO for representing a single Attachment.
 * Contains the generated URL for the frontend to use directly.
 */
@Data
@Builder
public class AttachmentDetailDTO {
    private Long id;
    private AttachmentType type;
    private String originalFileName;
    private String url; // MinIO pre-signed URL yoki YouTube embed URL
    private String contentType; // Rasm uchun foydali
    private Long size; // Rasm uchun foydali
    private LocalDateTime createdAt;
}