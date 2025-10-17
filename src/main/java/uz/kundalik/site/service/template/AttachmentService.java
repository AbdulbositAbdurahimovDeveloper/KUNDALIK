package uz.kundalik.site.service.template;

import org.springframework.web.multipart.MultipartFile;
import uz.kundalik.site.model.Attachment;
import uz.kundalik.site.payload.attachment.AttachmentCreatedResponseDTO;
import uz.kundalik.site.payload.attachment.AttachmentDetailDTO;
import uz.kundalik.site.payload.attachment.VideoRegisterDTO;

public interface AttachmentService {

    /**
     * Retrieves detailed information about an attachment by its ID,
     * including a usable URL.
     *
     * @param id The ID of the attachment.
     * @return A detailed DTO of the attachment.
     */
    AttachmentDetailDTO getById(Long id);

    /**
     * Handles the upload of an image file to MinIO storage.
     *
     * @param file The image file to upload.
     * @return A DTO containing the ID of the newly created attachment record.
     */
    AttachmentCreatedResponseDTO uploadImage(MultipartFile file);

    AttachmentCreatedResponseDTO uploadIcon(MultipartFile icon);


    /**
     * Registers a YouTube video in the system by its URL.
     *
     * @param registerDTO The DTO containing the YouTube URL and a title.
     * @return A DTO containing the ID of the newly created attachment record.
     */
    AttachmentCreatedResponseDTO registerVideo(VideoRegisterDTO registerDTO);

    /**
     * Deletes an attachment record from the database.
     * If the attachment is an image in MinIO, it also deletes the file from storage.
     *
     * @param id The ID of the attachment to delete.
     */
    void deleteById(Long id);

    Attachment saveImage(MultipartFile file);
}