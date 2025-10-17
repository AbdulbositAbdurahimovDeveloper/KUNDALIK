package uz.kundalik.telegram.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.site.exception.BadRequestException;
import uz.kundalik.site.exception.DataConflictException;
import uz.kundalik.site.exception.EntityNotFoundException;
import uz.kundalik.telegram.mapper.MessageTranslationMapper;
import uz.kundalik.telegram.model.Language;
import uz.kundalik.telegram.model.MessageKey;
import uz.kundalik.telegram.model.MessageTranslation;
import uz.kundalik.telegram.payload.i18n.*;
import uz.kundalik.telegram.repository.LanguageRepository;
import uz.kundalik.telegram.repository.MessageKeyRepository;
import uz.kundalik.telegram.repository.MessageTranslationRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageTranslationServiceImpl implements MessageTranslationService {

    private final MessageTranslationRepository translationRepository;
    private final MessageKeyRepository messageKeyRepository;
    private final LanguageRepository languageRepository;
    private final MessageTranslationMapper translationMapper;

    @Override
    @Transactional
    public MessageTranslationResponseDTO create(MessageTranslationCreateDTO createDto) {
        // 1. Tarjima uchun kalit va til mavjudligini tekshiramiz
        MessageKey messageKey = messageKeyRepository.findById(createDto.getMessageKeyId())
                .orElseThrow(() -> new EntityNotFoundException("MessageKey not found " + createDto.getMessageKeyId()));

        Language language = languageRepository.findById(createDto.getLanguageId())
                .orElseThrow(() -> new EntityNotFoundException("Language not found " + createDto.getLanguageId()));

        // 2. Nofaol tilga tarjima qo'shishga ruxsat bermaymiz
        if (!language.isActive()) {
            throw new BadRequestException("Cannot add translation for an inactive language: " + language.getName());
        }

        // 3. Bunday tarjima allaqachon mavjud emasligini tekshiramiz
        if (translationRepository.existsByMessageKeyIdAndLanguageId(messageKey.getId(), language.getId())) {
            throw new DataConflictException("Translation for key '" + messageKey.getKey() + "' in language '" + language.getCode() + "' already exists.");
        }

        MessageTranslation translation = new MessageTranslation();
        translation.setMessageKey(messageKey);
        translation.setLanguage(language);
        translation.setText(createDto.getText());

        MessageTranslation savedTranslation = translationRepository.save(translation);
        return translationMapper.toDTO(savedTranslation);
    }

    @Override
    public MessageTranslationResponseDTO getById(Long id) {
        return translationMapper.toDTO(findByIdOrElseThrow(id));
    }

    @Override
    public Page<MessageTranslationResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return translationRepository.findAll(pageable).map(translationMapper::toDTO);
    }

    @Override
    @Transactional
    public MessageTranslationResponseDTO update(Long id, MessageTranslationUpdateDTO updateDto) {
        MessageTranslation translation = findByIdOrElseThrow(id);
        translation.setText(updateDto.getText());
        MessageTranslation updatedTranslation = translationRepository.save(translation);
        return translationMapper.toDTO(updatedTranslation);
    }

    @Override
    public void delete(Long id) {
        if (!translationRepository.existsById(id)) {
            throw new EntityNotFoundException("MessageTranslation not found with id: " + id);
        }
        translationRepository.deleteById(id);
    }

    private MessageTranslation findByIdOrElseThrow(Long id) {
        return translationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MessageTranslation not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true) // Bu metod faqat o'qish uchun, shuning uchun readOnly=true optimizatsiya beradi
    public Map<String, Map<String, String>> getAllActiveTranslationsForFrontend() {
        // 1. Barcha aktiv tarjimalarni bitta so'rov bilan bazadan olamiz
        List<MessageTranslation> activeTranslations = translationRepository.findAllActiveTranslations();

        // 2. Ularni til kodi bo'yicha guruhlaymiz (groupingBy)
        return activeTranslations.stream()
                .collect(Collectors.groupingBy(
                        // Tashqi Map uchun kalit - bu tilning kodi (e.g., "uz")
                        translation -> translation.getLanguage().getCode(),
                        // Ichki Mapni yaratish. Kalit - message key (e.g., "welcome.message"),
                        // qiymat - tarjima matni (e.g., "Xush kelibsiz!")
                        Collectors.toMap(
                                translation -> translation.getMessageKey().getKey(),
                                MessageTranslation::getText
                        )
                ));
    }

    @Override
    @Transactional // Barcha operatsiyalar bitta tranzaksiyada bo'lishi SHART!
    public void bulkUpsert(BulkTranslationRequestDTO requestDto) {
        MessageKey messageKey = messageKeyRepository.findById(requestDto.getMessageKeyId())
                .orElseThrow(() -> new EntityNotFoundException("MessageKey" + requestDto.getMessageKeyId()));

        for (TranslationItemDTO item : requestDto.getTranslations()) {
            Language language = languageRepository.findById(item.getLanguageId())
                    .orElseThrow(() -> new EntityNotFoundException("Language" + item.getLanguageId()));

            if (!language.isActive()) {
                // Nofaol til uchun tarjimani o'tkazib yuboramiz yoki xatolik beramiz
                // Hozircha o'tkazib yuborish xavfsizroq
                continue;
            }

            // Mavjud tarjimani qidiramiz
            Optional<MessageTranslation> existingTranslationOpt = translationRepository
                    .findByMessageKey_KeyAndLanguage_Code(messageKey.getKey(), language.getCode());

            if (existingTranslationOpt.isPresent()) {
                // Agar mavjud bo'lsa, YANGILAYMIZ
                MessageTranslation existingTranslation = existingTranslationOpt.get();
                existingTranslation.setText(item.getText());
                translationRepository.save(existingTranslation);
            } else if (item.getText() != null && !item.getText().isBlank()) {
                // Agar mavjud bo'lmasa va matn bo'sh bo'lmasa, YANGI YARATAMIZ
                MessageTranslation newTranslation = new MessageTranslation();
                newTranslation.setMessageKey(messageKey);
                newTranslation.setLanguage(language);
                newTranslation.setText(item.getText());
                translationRepository.save(newTranslation);
            }
        }
    }
}