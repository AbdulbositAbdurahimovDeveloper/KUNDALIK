package uz.kundalik.telegram.service.message;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.site.exception.DataConflictException;
import uz.kundalik.site.exception.EntityNotFoundException;
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.telegram.mapper.MessageKeyMapper;
import uz.kundalik.telegram.model.MessageKey;
import uz.kundalik.telegram.payload.i18n.MessageKeyCreateDTO;
import uz.kundalik.telegram.payload.i18n.MessageKeyResponseDTO;
import uz.kundalik.telegram.payload.i18n.MessageKeyUpdateDTO;
import uz.kundalik.telegram.repository.MessageKeyRepository;
import uz.kundalik.telegram.repository.MessageTranslationRepository;

@Service
@RequiredArgsConstructor
public class MessageKeyServiceImpl implements MessageKeyService {

    private final MessageKeyRepository messageKeyRepository;
    private final MessageTranslationRepository messageTranslationRepository; // Buni keyingi qadamda ishlatamiz
    private final MessageKeyMapper messageKeyMapper;

    @Override
    public MessageKeyResponseDTO create(MessageKeyCreateDTO createDto) {
        if (messageKeyRepository.existsByKeyIgnoreCase(createDto.getKey())) {
            throw new DataConflictException("Message key '" + createDto.getKey() + "' already exists.");
        }

        MessageKey messageKey = new MessageKey();
        messageKey.setKey(createDto.getKey());
        messageKey.setDescription(createDto.getDescription());

        MessageKey savedKey = messageKeyRepository.save(messageKey);
        return messageKeyMapper.toDTO(savedKey);
    }

    @Override
    public MessageKeyResponseDTO getById(Long id) {
        return messageKeyMapper.toDTO(findByIdOrElseThrow(id));
    }

    @Override
    public PageDTO<MessageKeyResponseDTO> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("key").ascending());
        Page<MessageKey> messageKeys = messageKeyRepository.findAll(pageable);

        return new PageDTO<>(
                messageKeys.getContent().stream().map(messageKeyMapper::toDTO).toList(),
                messageKeys
        );
    }

    @Override
    @Transactional
    public MessageKeyResponseDTO update(Long id, MessageKeyUpdateDTO updateDto) {
        MessageKey messageKey = findByIdOrElseThrow(id);
        messageKey.setDescription(updateDto.getDescription());
        MessageKey updatedKey = messageKeyRepository.save(messageKey);
        return messageKeyMapper.toDTO(updatedKey);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        MessageKey messageKey = findByIdOrElseThrow(id);

        // MUHIM: Kalitni o'chirishdan oldin, unga bog'langan tarjimalar borligini tekshiramiz.
        // Agar tarjimalar bo'lsa, bu kalitni o'chirishga ruxsat bermaymiz.
        // Chunki bu "yetim" tarjimalarni qoldirib ketadi.
        if (messageTranslationRepository.existsByMessageKeyId(id)) {
            throw new DataConflictException("Cannot delete key '" + messageKey.getKey() + "' because it has existing translations. Please delete the translations first.");
        }

        messageKeyRepository.delete(messageKey);
    }

    private MessageKey findByIdOrElseThrow(Long id) {
        return messageKeyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MessageKey not found with: " + id));
    }
}