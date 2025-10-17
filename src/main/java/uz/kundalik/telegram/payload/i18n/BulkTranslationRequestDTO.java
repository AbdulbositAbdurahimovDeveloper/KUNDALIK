package uz.kundalik.telegram.payload.i18n;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class BulkTranslationRequestDTO {

    @NotNull(message = "Message key ID cannot be null")
    private Long messageKeyId;

    @Valid // Ichki elementlarni ham validatsiya qilish uchun
    @NotEmpty(message = "Translations list cannot be empty")
    private List<TranslationItemDTO> translations;
}