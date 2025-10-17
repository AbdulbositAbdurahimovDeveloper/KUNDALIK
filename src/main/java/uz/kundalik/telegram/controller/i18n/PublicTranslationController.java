package uz.kundalik.telegram.controller.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.kundalik.site.payload.response.ResponseDTO;
import uz.kundalik.telegram.service.message.MessageTranslationService;

import java.util.Map;

/**
 * This controller provides public-facing endpoints for retrieving translation resources.
 * These endpoints are typically consumed by frontend applications upon initialization
 * to load all necessary UI text without requiring authentication.
 */
@RestController
@RequestMapping("/api/v1/translations/public")
@RequiredArgsConstructor
public class PublicTranslationController {

    private final MessageTranslationService translationService;

    /**
     * Fetches all active translations formatted for easy consumption by i18n libraries on the frontend.
     *
     * @return A ResponseDTO containing a nested map of translations: { "uz": { "key": "value" }, "en": { "key": "value" } }
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDTO<Map<String, Map<String, String>>>> getAllPublicTranslations() {
        Map<String, Map<String, String>> translations = translationService.getAllActiveTranslationsForFrontend();
        return ResponseEntity.ok(ResponseDTO.success(translations));
    }
}