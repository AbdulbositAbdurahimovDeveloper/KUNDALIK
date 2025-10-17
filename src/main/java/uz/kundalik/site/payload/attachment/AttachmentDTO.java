package uz.kundalik.site.payload.attachment;

import lombok.Builder;
import lombok.Data;
import uz.kundalik.site.enums.AttachmentType;

/**
 * DTO for {@link uz.kundalik.site.model.Attachment}
 */
@Data
@Builder
public class AttachmentDTO {

    private Long id;
    private AttachmentType type;
    private String url;
}
