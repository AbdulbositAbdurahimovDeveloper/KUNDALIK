package uz.kundalik.site.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.kundalik.site.enums.AttachmentType;
import uz.kundalik.site.exception.DataConflictException;
import uz.kundalik.site.exception.EntityNotFoundException;
import uz.kundalik.site.mapper.AttachmentMapper;
import uz.kundalik.site.model.Attachment;
import uz.kundalik.site.payload.attachment.AttachmentCreatedResponseDTO;
import uz.kundalik.site.payload.attachment.AttachmentDetailDTO;
import uz.kundalik.site.payload.attachment.VideoRegisterDTO;
import uz.kundalik.site.properties.MinioProperties;
import uz.kundalik.site.repository.AttachmentRepository;
import uz.kundalik.site.service.minio.MinioService;
import uz.kundalik.site.service.template.AttachmentService;
import uz.kundalik.site.util.YouTubeUrlUtil;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final MinioService minioService;
    private final MinioProperties minioProperties;

    /**
     * Retrieves detailed information about an attachment by its ID,
     * including a usable URL.
     *
     * @param id The ID of the attachment.
     * @return A detailed DTO of the attachment.
     */
    @Override
    @Transactional(readOnly = true)
    public AttachmentDetailDTO getById(Long id) {
        log.info("Fetching attachment details for id: {}", id);
        Attachment attachment = findAttachmentById(id);
        return attachmentMapper.toDetailDTO(attachment);
    }

    @Override
    @Transactional
    public AttachmentCreatedResponseDTO uploadImage(MultipartFile file) {
        validateImageFile(file);

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = StringUtils.getFilenameExtension(originalFileName);
        String objectKey = UUID.randomUUID() + "." + extension;

        log.info("Uploading image '{}' to MinIO with key '{}'", originalFileName, objectKey);
        minioService.uploadFile(objectKey, file);

        Attachment attachment = Attachment.builder()
                .type(AttachmentType.IMAGE)
                .originalFileName(originalFileName)
                .objectKey(objectKey)
                .bucketName(minioProperties.getBuckets().get(0))
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        log.info("Saved image attachment record with id: {}", savedAttachment.getId());

        return attachmentMapper.toCreatedResponseDTO(savedAttachment);
    }

    @Override
    public AttachmentCreatedResponseDTO uploadIcon(MultipartFile icon) {
        return null;
    }

    @Override
    @Transactional
    public AttachmentCreatedResponseDTO registerVideo(VideoRegisterDTO registerDTO) {
        log.info("Registering video from URL: {}", registerDTO.getYoutubeUrl());

        String videoId = YouTubeUrlUtil.extractVideoId(registerDTO.getYoutubeUrl());
        if (videoId == null) {
            throw new DataConflictException("Invalid YouTube URL provided: " + registerDTO.getYoutubeUrl());
        }

        Attachment attachment = Attachment.builder()
                .type(AttachmentType.VIDEO)
                .originalFileName(registerDTO.getTitle())
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        log.info("Saved video attachment record with id: {}", savedAttachment.getId());

        return attachmentMapper.toCreatedResponseDTO(savedAttachment);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.warn("Attempting to delete attachment with id: {}", id);
        Attachment attachment = findAttachmentById(id);

        if (attachment.getType() == AttachmentType.IMAGE) {
            log.info("Attachment is an image. Deleting file '{}' from MinIO bucket '{}'", attachment.getObjectKey(), attachment.getBucketName());
            minioService.deleteFile(attachment.getBucketName(), attachment.getObjectKey());
        }

        attachmentRepository.delete(attachment);
        log.info("Successfully deleted attachment record with id: {}", id);
    }

    @Override
    public Attachment saveImage(MultipartFile file) {

        validateImageFile(file);

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = StringUtils.getFilenameExtension(originalFileName);
        String objectKey = UUID.randomUUID() + "." + extension;

        minioService.uploadFile(objectKey, file);

        Attachment attachment = Attachment.builder()
                .type(AttachmentType.IMAGE)
                .originalFileName(originalFileName)
                .objectKey(objectKey)
                .bucketName(minioProperties.getBuckets().get(0))
                .contentType(file.getContentType())
                .size(file.getSize())
                .build();

        return attachmentRepository.save(attachment);

    }

    // --- Private Helper Methods ---

    private Attachment findAttachmentById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attachment not found with id: " + id));
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new DataConflictException("File cannot be empty.");
        }
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            throw new DataConflictException("Invalid file type. Only images are allowed. Provided type: " + file.getContentType());
        }
        // Max size validation (e.g., 10MB)
        long maxSizeInBytes = 10 * 1024 * 1024;
        if (file.getSize() > maxSizeInBytes) {
            throw new DataConflictException("File size exceeds the limit of 10MB.");
        }
    }
}