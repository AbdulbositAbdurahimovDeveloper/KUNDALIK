package uz.kundalik.telegram.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.telegram.model.Language;
import uz.kundalik.telegram.model.MessageKey;
import uz.kundalik.telegram.model.MessageTranslation;
import uz.kundalik.telegram.repository.LanguageRepository;
import uz.kundalik.telegram.repository.MessageKeyRepository;
import uz.kundalik.telegram.repository.MessageTranslationRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class i18nImpl implements i18n {

    private final LanguageRepository languageRepository;
    private final MessageKeyRepository messageKeyRepository;
    private final MessageTranslationRepository messageTranslationRepository;

    /**
     * Xabarni tildagi tarjimasi bilan qaytaradi.
     * Agar mavjud bo‘lmasa, default tildagi versiyani yoki key nomini qaytaradi.
     */
    @Override
    @Cacheable(value = "translations", key = "#key + '_' + #langCode")
    public String get(String key, String langCode) {
        // 1️⃣ Tilni topamiz
        Optional<Language> languageOpt = languageRepository.findByCodeAndActiveTrue(langCode);
        if (languageOpt.isEmpty()) {
            return getDefault(key);
        }

        // 2️⃣ MessageKey ni topamiz
        Optional<MessageKey> keyOpt = messageKeyRepository.findByKey(key);
        if (keyOpt.isEmpty()) {
            return "[" + key + "]";
        }

        // 3️⃣ Tarjimani topamiz
        Optional<MessageTranslation> translationOpt = messageTranslationRepository
                .findByMessageKeyAndLanguage(keyOpt.get(), languageOpt.get());

        return translationOpt
                .map(MessageTranslation::getText)
                .orElseGet(() -> getDefault(key));
    }

    /**
     * Default tildagi tarjimani qaytaradi yoki key nomini o‘zi.
     */
    @Override
    @Cacheable(value = "translations", key = "#key + '_default'")
    public String getDefault(String key) {
        Optional<Language> defaultLangOpt = languageRepository.findByDefaultLanguageTrue();
        if (defaultLangOpt.isEmpty()) {
            return "[" + key + "]";
        }

        Optional<MessageKey> keyOpt = messageKeyRepository.findByKey(key);
        if (keyOpt.isEmpty()) {
            return "[" + key + "]";
        }

        Optional<MessageTranslation> translationOpt = messageTranslationRepository
                .findByMessageKeyAndLanguage(keyOpt.get(), defaultLangOpt.get());

        return translationOpt.map(MessageTranslation::getText)
                .orElse("[" + key + "]");
    }
}
