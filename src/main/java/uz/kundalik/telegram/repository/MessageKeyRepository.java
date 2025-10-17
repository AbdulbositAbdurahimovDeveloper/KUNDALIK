package uz.kundalik.telegram.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.kundalik.telegram.model.MessageKey;

import java.util.Optional;

@Repository
public interface MessageKeyRepository extends JpaRepository<MessageKey, Long> {
    Optional<MessageKey> findByKey(String key);

    boolean existsByKey(@NotBlank(message = "Message key is required") String key);

    boolean existsByKeyIgnoreCase(String key);
}