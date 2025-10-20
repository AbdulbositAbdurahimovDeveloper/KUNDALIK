package uz.kundalik.telegram.service.message.dev;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.telegram.model.Language;
import uz.kundalik.telegram.model.MessageKey;
import uz.kundalik.telegram.model.MessageTranslation;
import uz.kundalik.telegram.repository.LanguageRepository;
import uz.kundalik.telegram.repository.MessageKeyRepository;
import uz.kundalik.telegram.repository.MessageTranslationRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class JsonI18nServiceImpl implements I18nService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final LanguageRepository languageRepository;
    private final MessageKeyRepository messageKeyRepository;
    private final MessageTranslationRepository messageTranslationRepository;

    // Barcha tarjimalarni xotirada saqlash uchun Map
    private Map<String, Map<String, String>> translations = new ConcurrentHashMap<>();

    public JsonI18nServiceImpl(ResourceLoader resourceLoader, ObjectMapper objectMapper, LanguageRepository languageRepository, MessageKeyRepository messageKeyRepository, MessageTranslationRepository messageTranslationRepository) {
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.languageRepository = languageRepository;
        this.messageKeyRepository = messageKeyRepository;
        this.messageTranslationRepository = messageTranslationRepository;
    }

    /**
     * Bu metod Spring bean'ni yaratib bo'lgandan so'ng avtomatik ishga tushadi.
     * JSON faylni o'qiydi va ma'lumotlarni 'translations' Map'iga yuklaydi.
     */
    @PostConstruct
    public void loadTranslations() {
        String filePath = "classpath:other/language/language.json";
        try {
            Resource resource = resourceLoader.getResource(filePath);
            InputStream inputStream = resource.getInputStream();
            // JSONni Map<String, Map<String, String>> turiga o'giramiz
            this.translations = objectMapper.readValue(inputStream, new TypeReference<>() {});
            log.info("Successfully loaded translations from {}", filePath);
        } catch (IOException e) {
            log.error("Failed to load translations from file: {}", filePath, e);
            // Muhim fayl bo'lsa, bu yerda dasturni to'xtatish uchun exception tashlash mumkin
            // throw new RuntimeException("Could not initialize I18n service", e);
        }
    }

    @Override
    public String get(String key, String langCode) {
        // 1. So'ralgan tilda tarjimani qidiramiz
        String message = getMessageFromLang(key, langCode);

        // 3. Agar standart tilda ham topilmasa, kalitning o'zini qaytaramiz
        return message != null ? message : key;
    }

    @Override
    public String get(String key, String langCode, Object... args) {
        String template = get(key, langCode);
        return String.format(template, args);
    }

    /**
     * Map'dan xabarni olish uchun yordamchi metod
     */
    private String getMessageFromLang(String key, String langCode) {
        if (langCode == null || key == null) {
            return null;
        }
        return translations.getOrDefault(langCode, Map.of()).get(key);
    }


    /**
     * JSON fayldagi barcha tarjimalarni o'qib, ma'lumotlar bazasiga sinxronizatsiya qiladi.
     * Mavjud bo'lmagan til, kalit yoki tarjimalarni yaratadi.
     * Mavjud tarjimalarni yangilaydi.
     */
    @Transactional
    public void synchronizeJsonToDb() {
        if (translations.isEmpty()) {
            log.warn("Translations map is empty. Nothing to synchronize.");
            return;
        }

        log.info("Starting synchronization from JSON to Database...");

        for (Map.Entry<String, Map<String, String>> langEntry : translations.entrySet()) {
            String langCode = langEntry.getKey();
            Map<String, String> messages = langEntry.getValue();

            // 1. Tilni topish yoki yaratish
            Language language = languageRepository.findByCode(langCode)
                    .orElseGet(() -> {
                        log.info("Language with code '{}' not found. Creating new one.", langCode);
                        Language newLang = new Language();
                        newLang.setCode(langCode);
                        newLang.setName(langCode.toUpperCase()); // Vaqtinchalik nom
                        return languageRepository.save(newLang);
                    });

            for (Map.Entry<String, String> messageEntry : messages.entrySet()) {
                String keyString = messageEntry.getKey();
                String text = messageEntry.getValue();

                // 2. Kalit so'zni (MessageKey) topish yoki yaratish
                MessageKey messageKey = messageKeyRepository.findByKey(keyString)
                        .orElseGet(() -> {
                            log.info("MessageKey with key '{}' not found. Creating new one.", keyString);
                            MessageKey newKey = new MessageKey();
                            newKey.setKey(keyString);
                            return messageKeyRepository.save(newKey);
                        });

                // 3. Tarjimani (MessageTranslation) topish va yangilash yoki yaratish
                MessageTranslation translation = messageTranslationRepository
                        .findByMessageKeyAndLanguage(messageKey, language)
                        .map(existingTranslation -> {
                            // Agar tarjima matni o'zgargan bo'lsa, yangilaymiz
                            if (!existingTranslation.getText().equals(text)) {
                                log.info("Updating translation for key '{}' in language '{}'", keyString, langCode);
                                existingTranslation.setText(text);
                                return existingTranslation; // save() keyinroq chaqiriladi
                            }
                            return existingTranslation;
                        })
                        .orElseGet(() -> {
                            log.info("Creating new translation for key '{}' in language '{}'", keyString, langCode);
                            MessageTranslation newTranslation = new MessageTranslation();
                            newTranslation.setLanguage(language);
                            newTranslation.setMessageKey(messageKey);
                            newTranslation.setText(text);
                            return newTranslation;
                        });

                messageTranslationRepository.save(translation);
            }
        }
        log.info("Synchronization from JSON to Database finished successfully.");
    }

}