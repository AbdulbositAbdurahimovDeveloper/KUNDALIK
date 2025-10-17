package uz.kundalik.telegram.service.message;

import org.springframework.data.domain.Page;
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.telegram.payload.i18n.LanguageCreateDTO;
import uz.kundalik.telegram.payload.i18n.LanguageResponseDTO;
import uz.kundalik.telegram.payload.i18n.LanguageUpdateDTO;

public interface LanguageService {

    LanguageResponseDTO create(LanguageCreateDTO createDto);

    LanguageResponseDTO getById(Long id);

    PageDTO<LanguageResponseDTO> getAll(int page, int size);

    LanguageResponseDTO update(Long id, LanguageUpdateDTO updateDto);

    void delete(Long id);

    LanguageResponseDTO setActive(Long id, boolean active);

    LanguageResponseDTO setDefault(Long id);
}