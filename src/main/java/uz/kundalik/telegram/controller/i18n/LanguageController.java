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
import uz.kundalik.telegram.payload.i18n.LanguageCreateDTO;
import uz.kundalik.telegram.payload.i18n.LanguageResponseDTO;
import uz.kundalik.telegram.payload.i18n.LanguageUpdateDTO;
import uz.kundalik.telegram.service.message.LanguageService;

@RestController
@RequestMapping("/api/v1/languages")
@RequiredArgsConstructor
public class LanguageController {

    private final LanguageService languageService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<LanguageResponseDTO>> createLanguage(@Valid @RequestBody LanguageCreateDTO createDto) {
        LanguageResponseDTO result = languageService.create(createDto);
        return ResponseEntity.ok(ResponseDTO.success(result, "Language created successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<LanguageResponseDTO>> getLanguageById(@PathVariable Long id) {
        LanguageResponseDTO result = languageService.getById(id);
        return ResponseEntity.ok(ResponseDTO.success(result));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<PageDTO<LanguageResponseDTO>>> getAllLanguages(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageDTO<LanguageResponseDTO> pageDTO = languageService.getAll(page, size);
        return ResponseEntity.ok(ResponseDTO.success(pageDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<LanguageResponseDTO>> updateLanguage(@PathVariable Long id, @Valid @RequestBody LanguageUpdateDTO updateDto) {
        LanguageResponseDTO result = languageService.update(id, updateDto);
        return ResponseEntity.ok(ResponseDTO.success(result, "Language updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<Void>> deleteLanguage(@PathVariable Long id) {
        languageService.delete(id);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(ResponseDTO.success(null, "Language deleted successfully"));
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<LanguageResponseDTO>> setLanguageActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        LanguageResponseDTO result = languageService.setActive(id, active);
        String message = "Language " + (active ? "activated" : "deactivated") + " successfully";
        return ResponseEntity.ok(ResponseDTO.success(result, message));
    }

    @PatchMapping("/{id}/default")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<LanguageResponseDTO>> setLanguageAsDefault(@PathVariable Long id) {
        LanguageResponseDTO result = languageService.setDefault(id);
        return ResponseEntity.ok(ResponseDTO.success(result, "Language set as default successfully"));
    }
}