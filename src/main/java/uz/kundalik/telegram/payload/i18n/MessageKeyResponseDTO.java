package uz.kundalik.telegram.payload.i18n;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageKeyResponseDTO {
    private Long id;
    private String key;
    private String description;
}