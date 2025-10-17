package uz.kundalik.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.kundalik.telegram.model.Language;
import uz.kundalik.telegram.model.MessageKey;
import uz.kundalik.telegram.model.MessageTranslation;

import java.util.List;
import java.util.Optional;

public interface MessageTranslationRepository extends JpaRepository<MessageTranslation, Long> {

    Optional<MessageTranslation> findByMessageKeyAndLanguage(MessageKey key, Language language);

    boolean existsByMessageKeyId(Long id);

    // Biror tilga bog'liq tarjimalar mavjudligini tekshirish (LanguageService uchun)
    boolean existsByLanguageId(Long languageId);

    // Muayyan kalit va til uchun tarjima allaqachon mavjudligini tekshirish
    boolean existsByMessageKeyIdAndLanguageId(Long messageKeyId, Long languageId);

    // Barcha aktiv tillardagi tarjimalarni frontend uchun olib berish
    @Query("SELECT mt FROM MessageTranslation mt JOIN FETCH mt.language l JOIN FETCH mt.messageKey mk WHERE l.active = true")
    List<MessageTranslation> findAllActiveTranslations();

    // Kalit va til bo'yicha tarjimani topish
    Optional<MessageTranslation> findByMessageKey_KeyAndLanguage_Code(String key, String langCode);
}