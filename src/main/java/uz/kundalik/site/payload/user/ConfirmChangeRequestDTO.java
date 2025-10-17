package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConfirmChangeRequestDTO {

    @NotBlank(message = "Tasdiqlash kodi bo'sh bo'lishi mumkin emas")
    @Size(min = 4, max = 6, message = "Kod 4-6 xonali raqam bo'lishi kerak")
    private String code;
}