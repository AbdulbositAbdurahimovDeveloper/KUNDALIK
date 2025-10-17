package uz.kundalik.site.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyPhoneRequestDTO {

    @NotBlank(message = "Telefon raqami bo'sh bo'lishi mumkin emas")
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Telefon raqam formati noto'g'ri (Masalan: +998901234567)")
    private String phoneNumber;

    @NotBlank(message = "Kod bo'sh bo'lishi mumkin emas")
    @Size(min = 4, max = 6, message = "Kod 4-6 xonali raqam bo'lishi kerak")
    private String code;
}