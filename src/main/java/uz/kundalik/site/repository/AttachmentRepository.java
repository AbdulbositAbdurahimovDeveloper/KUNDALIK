package uz.kundalik.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.kundalik.site.model.Attachment;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
}