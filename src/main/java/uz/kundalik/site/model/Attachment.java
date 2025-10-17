package uz.kundalik.site.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.kundalik.site.enums.AttachmentType;
import uz.kundalik.site.model.Abs.AbsAuditingSoftDeleteEntity;
import uz.kundalik.site.model.Abs.AbsDateEntity;
import uz.kundalik.site.model.Abs.AbsLongEntity;

/**
 * Represents a file attachment, which can be either an image stored on MinIO
 * or a video hosted on YouTube.
 *
 * This entity uses a discriminator column 'type' to distinguish between
 * different kinds of attachments and stores the relevant metadata for each.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true) // To include parent fields in toString
@Entity
@Table(name = "attachments")
@SQLDelete(sql = "UPDATE attachments SET deleted = true WHERE id = ?")
public class Attachment extends AbsAuditingSoftDeleteEntity {

    /**
     * The original name of the file as uploaded by the user (e.g., "beautiful_sanatorium.jpg").
     * Useful for display purposes.
     */
    @Column(nullable = false)
    private String originalFileName;

    /**
     * Discriminator column. Determines the type of attachment (IMAGE or VIDEO)
     * and which metadata fields below are relevant.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AttachmentType type;

    // --- Common Metadata ---
    /**
     * The content type of the originally uploaded file (e.g., "image/jpeg").
     * Can be nullable as YouTube videos won't have a direct content type in our system.
     */
    private String contentType;

    /**
     * The size of the originally uploaded file in bytes.
     * Can be nullable as we don't store the YouTube video file itself.
     */
    private Long size;

    // --- Metadata for IMAGE type (stored in MinIO) ---
    /**
     * The name of the bucket in MinIO where the image is stored.
     * Null for VIDEO type.
     */
    private String bucketName;

    /**
     * The unique key (generated file name) for the object in MinIO.
     * Null for VIDEO type.
     */
    private String objectKey;


    // --- Optional Telegram Cache Metadata ---
    /**
     * The unique file_id from Telegram. Can be used for both images and videos
     * if they are sent to a Telegram channel. Nullable.
     */
    @Column(name = "telegram_file_id")
    private String telegramFileId;

    @Column(name = "telegram_message_id")
    private Integer telegramMessageId;

    @Transient
    private String url;

}