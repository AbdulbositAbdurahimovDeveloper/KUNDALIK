package uz.kundalik.telegram.payload.i18n;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TranslationItemDTO {
    @NotNull(message = "Language ID cannot be null")
    private Long languageId;
    
    // Matn bo'sh bo'lishi mumkin, shuning uchun @NotBlank yo'q
    private String text; 
}