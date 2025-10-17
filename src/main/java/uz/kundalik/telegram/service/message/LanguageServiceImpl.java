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
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.telegram.model.Language;
import uz.kundalik.telegram.payload.i18n.LanguageCreateDTO;
import uz.kundalik.telegram.payload.i18n.LanguageResponseDTO;
import uz.kundalik.telegram.payload.i18n.LanguageUpdateDTO;
import uz.kundalik.telegram.repository.LanguageRepository;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final LanguageMapper languageMapper;

    @Override
    @Transactional
    public LanguageResponseDTO create(LanguageCreateDTO createDto) {
        // 1. Til kodining noyobligini tekshirish
        if (languageRepository.existsByCodeIgnoreCase(createDto.getCode())) {
            throw new DataConflictException("Language with code '" + createDto.getCode() + "' already exists.");
        }

        // 2. Agar yangi til standart qilib belgilanayotgan bo'lsa...
        if (createDto.getDefaultLanguage()) {
            // ... boshqa standart til borligini tekshiramiz va uning "standart" belgisini olib tashlaymiz
            languageRepository.findByDefaultLanguageTrue().ifPresent(lang -> {
                lang.setDefaultLanguage(false);
                languageRepository.save(lang);
            });
        } else {
            // 3. Agar bu birinchi til bo'lsa va standart qilib belgilanmagan bo'lsa, xatolik beramiz
            if (languageRepository.count() == 0) {
                throw new BadRequestException("The first language must be set as default.");
            }
        }

        Language language = new Language();
        language.setCode(createDto.getCode());
        language.setName(createDto.getName());
        language.setActive(createDto.getActive());
        language.setDefaultLanguage(createDto.getDefaultLanguage());

        Language savedLanguage = languageRepository.save(language);
        return languageMapper.toDTO(savedLanguage);
    }

    @Override
    public LanguageResponseDTO getById(Long id) {
        Language language = findByIdOrElseThrow(id);
        return languageMapper.toDTO(language);
    }

    @Override
    public PageDTO<LanguageResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Language> languages = languageRepository.findAll(pageable);

        return new PageDTO<>(
                languages.map(languageMapper::toDTO).getContent(),
                languages
        );
    }

    @Override
    @Transactional
    public LanguageResponseDTO update(Long id, LanguageUpdateDTO updateDto) {
        Language language = findByIdOrElseThrow(id);

        language.setName(updateDto.getName());
        language.setActive(updateDto.getActive());

        // Agar nofaol qilinayotgan til standart til bo'lsa, xatolik beramiz
        if (!language.isActive() && language.isDefaultLanguage()) {
            throw new BadRequestException("The default language cannot be deactivated.");
        }

        Language updatedLanguage = languageRepository.save(language);
        return languageMapper.toDTO(updatedLanguage);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Language language = findByIdOrElseThrow(id);

        // Standart tilni o'chirishga ruxsat bermaymiz
        if (language.isDefaultLanguage()) {
            throw new BadRequestException("Cannot delete the default language. Please set another language as default first.");
        }

        // TODO: Kelajakda bu tilga bog'liq tarjimalar bor yoki yo'qligini tekshirish kerak bo'ladi.
        // Hozircha shu holatda qoldiramiz.

        languageRepository.delete(language);
    }

    @Override
    @Transactional
    public LanguageResponseDTO setActive(Long id, boolean active) {
        Language language = findByIdOrElseThrow(id);

        // Agar nofaol qilinayotgan til standart til bo'lsa, xatolik beramiz
        if (!active && language.isDefaultLanguage()) {
            throw new BadRequestException("The default language cannot be deactivated.");
        }

        language.setActive(active);
        Language savedLanguage = languageRepository.save(language);
        return languageMapper.toDTO(savedLanguage);
    }

    @Override
    @Transactional
    public LanguageResponseDTO setDefault(Long id) {
        Language newDefaultLanguage = findByIdOrElseThrow(id);

        // Yangi standart til aktiv bo'lishi shart
        if (!newDefaultLanguage.isActive()) {
            throw new BadRequestException("Only active languages can be set as default.");
        }

        // Barcha boshqa tillardan standart belgisini olib tashlaymiz
        languageRepository.resetAllDefaultLanguages(id);

        // Yangi tilni standart qilib belgilaymiz
        newDefaultLanguage.setDefaultLanguage(true);
        Language savedLanguage = languageRepository.save(newDefaultLanguage);

        return languageMapper.toDTO(savedLanguage);
    }

    // Yordamchi metod
    private Language findByIdOrElseThrow(Long id) {
        return languageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Language not found with id" + id));
    }
}