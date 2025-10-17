package uz.kundalik.telegram.payload.i18n;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageTranslationCreateDTO {

    @NotNull(message = "Message key ID cannot be null")
    private Long messageKeyId;

    @NotNull(message = "Language ID cannot be null")
    private Long languageId;

    @NotBlank(message = "Translation text cannot be blank")
    private String text;
}