package uz.kundalik.telegram.controller.i18n;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.site.payload.response.ResponseDTO;
import uz.kundalik.telegram.payload.i18n.MessageKeyCreateDTO;
import uz.kundalik.telegram.payload.i18n.MessageKeyResponseDTO;
import uz.kundalik.telegram.payload.i18n.MessageKeyUpdateDTO;
import uz.kundalik.telegram.service.message.MessageKeyService;

@RestController
@RequestMapping("/api/v1/message-keys")
@RequiredArgsConstructor
public class MessageKeyController {

    private final MessageKeyService messageKeyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<MessageKeyResponseDTO>> createMessageKey(@Valid @RequestBody MessageKeyCreateDTO createDto) {
        MessageKeyResponseDTO result = messageKeyService.create(createDto);
        return ResponseEntity.ok(ResponseDTO.success(result, "Message key created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<MessageKeyResponseDTO>> getMessageKeyById(@PathVariable Long id) {
        MessageKeyResponseDTO result = messageKeyService.getById(id);
        return ResponseEntity.ok(ResponseDTO.success(result));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PageDTO<MessageKeyResponseDTO>>> getAllMessageKeys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageDTO<MessageKeyResponseDTO> pageDTO = messageKeyService.getAll(page, size);
        return ResponseEntity.ok(ResponseDTO.success(pageDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<MessageKeyResponseDTO>> updateMessageKey(
            @PathVariable Long id,
            @Valid @RequestBody MessageKeyUpdateDTO updateDto) {
        MessageKeyResponseDTO result = messageKeyService.update(id, updateDto);
        return ResponseEntity.ok(ResponseDTO.success(result, "Message key updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> deleteMessageKey(@PathVariable Long id) {
        messageKeyService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ResponseDTO.success(null, "Message key deleted successfully"));
    }
}