package uz.kundalik.telegram.service.message;

import org.springframework.data.domain.Page;
import uz.kundalik.telegram.payload.i18n.BulkTranslationRequestDTO;
import uz.kundalik.telegram.payload.i18n.MessageTranslationCreateDTO;
import uz.kundalik.telegram.payload.i18n.MessageTranslationResponseDTO;
import uz.kundalik.telegram.payload.i18n.MessageTranslationUpdateDTO;

import java.util.Map;

public interface MessageTranslationService {

    MessageTranslationResponseDTO create(MessageTranslationCreateDTO createDto);

    MessageTranslationResponseDTO getById(Long id);

    Page<MessageTranslationResponseDTO> getAll(int page, int size);

    MessageTranslationResponseDTO update(Long id, MessageTranslationUpdateDTO updateDto);

    void delete(Long id);

    /**
     * Retrieves all active translations grouped by language code.
     * This is designed for public consumption by frontend applications to fetch all necessary UI text in one call.
     * The structure is a map where the key is the language code (e.g., "en", "uz") and the value is another
     * map of message keys to their translated text.
     *
     * @return A map structured as {@code Map<LanguageCode, Map<MessageKey, TranslatedText>>}.
     */
    Map<String, Map<String, String>> getAllActiveTranslationsForFrontend();

    void bulkUpsert(BulkTranslationRequestDTO requestDto);
}