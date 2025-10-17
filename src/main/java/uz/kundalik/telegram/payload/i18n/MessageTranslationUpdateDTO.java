package uz.kundalik.telegram.payload.i18n;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MessageTranslationUpdateDTO {

    @NotBlank(message = "Translation text cannot be blank")
    private String text;
}