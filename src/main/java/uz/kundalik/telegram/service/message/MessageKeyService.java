package uz.kundalik.telegram.service.message;

import org.springframework.data.domain.Page;
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.telegram.payload.i18n.MessageKeyCreateDTO;
import uz.kundalik.telegram.payload.i18n.MessageKeyResponseDTO;
import uz.kundalik.telegram.payload.i18n.MessageKeyUpdateDTO;

public interface MessageKeyService {

    MessageKeyResponseDTO create(MessageKeyCreateDTO createDto);

    MessageKeyResponseDTO getById(Long id);

    PageDTO<MessageKeyResponseDTO> getAll(int page, int size);

    MessageKeyResponseDTO update(Long id, MessageKeyUpdateDTO updateDto);

    void delete(Long id);
}