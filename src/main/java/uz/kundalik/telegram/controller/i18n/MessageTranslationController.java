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
import uz.kundalik.telegram.payload.i18n.BulkTranslationRequestDTO;
import uz.kundalik.telegram.payload.i18n.MessageTranslationCreateDTO;
import uz.kundalik.telegram.payload.i18n.MessageTranslationResponseDTO;
import uz.kundalik.telegram.payload.i18n.MessageTranslationUpdateDTO;
import uz.kundalik.telegram.service.message.MessageTranslationService;

@RestController
@RequestMapping("/api/v1/translations")
@RequiredArgsConstructor
public class MessageTranslationController {

    private final MessageTranslationService translationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<MessageTranslationResponseDTO>> createTranslation(@Valid @RequestBody MessageTranslationCreateDTO createDto) {
        MessageTranslationResponseDTO result = translationService.create(createDto);
        return ResponseEntity.ok(ResponseDTO.success(result, "Translation created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<MessageTranslationResponseDTO>> getTranslationById(@PathVariable Long id) {
        MessageTranslationResponseDTO result = translationService.getById(id);
        return ResponseEntity.ok(ResponseDTO.success(result));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PageDTO<MessageTranslationResponseDTO>>> getAllTranslations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MessageTranslationResponseDTO> resultPage = translationService.getAll(page, size);
        PageDTO<MessageTranslationResponseDTO> pageDTO = new PageDTO<>(resultPage.getContent(), resultPage);
        return ResponseEntity.ok(ResponseDTO.success(pageDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<MessageTranslationResponseDTO>> updateTranslation(
            @PathVariable Long id,
            @Valid @RequestBody MessageTranslationUpdateDTO updateDto) {
        MessageTranslationResponseDTO result = translationService.update(id, updateDto);
        return ResponseEntity.ok(ResponseDTO.success(result, "Translation updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> deleteTranslation(@PathVariable Long id) {
        translationService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ResponseDTO.success(null, "Translation deleted successfully"));
    }

    @PostMapping("/bulk-upsert")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> bulkUpsertTranslations(@Valid @RequestBody BulkTranslationRequestDTO requestDto) {
        translationService.bulkUpsert(requestDto);
        return ResponseEntity.ok(ResponseDTO.success(null, "Translations updated successfully"));
    }
}