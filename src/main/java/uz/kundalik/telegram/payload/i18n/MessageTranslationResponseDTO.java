package uz.kundalik.telegram.payload.i18n;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageTranslationResponseDTO {
    private Long id;
    private LanguageResponseDTO language; // Til haqida to'liq ma'lumot
    private MessageKeyResponseDTO messageKey; // Kalit haqida to'liq ma'lumot
    private String text;
}