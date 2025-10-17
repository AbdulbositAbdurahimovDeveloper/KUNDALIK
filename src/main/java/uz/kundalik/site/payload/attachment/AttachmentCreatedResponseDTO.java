package uz.kundalik.site.payload.attachment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.kundalik.site.enums.AttachmentType;

/**
 * DTO returned after successfully creating an attachment (image upload or video registration).
 * Provides the essential ID needed to associate this attachment with other entities.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentCreatedResponseDTO {
    private Long id;
    private AttachmentType type;
    private String originalFileName;
}