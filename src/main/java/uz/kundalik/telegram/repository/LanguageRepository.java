package uz.kundalik.telegram.repository;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.kundalik.site.exception.EntityNotFoundException;
import uz.kundalik.telegram.model.Language;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {
    Optional<Language> findByCodeAndActiveTrue(String code);

    Optional<Language> findByDefaultLanguageTrue();

    default Language findByDefaultLanguageTrueOrElseThrow() {
        return findByDefaultLanguageTrue().orElseThrow(() -> new EntityNotFoundException("Language not found"));
    }

    Optional<Language> findByCode(@NotBlank(message = "Language code is required") String languageCode);

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    // Standart tilni topish uchun

    // Barcha standart tillar belgisini o'chirish uchun
    @Modifying
    @Query("UPDATE Language l SET l.defaultLanguage = false WHERE l.defaultLanguage = true AND l.id <> :newDefaultLanguageId")
    void resetAllDefaultLanguages(@Param("newDefaultLanguageId") Long newDefaultLanguageId);

}