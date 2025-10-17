package uz.kundalik.telegram.payload.i18n;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageResponseDTO {
    private Long id;
    private String code;
    private String name;
    private boolean active;
    private boolean defaultLanguage;
}