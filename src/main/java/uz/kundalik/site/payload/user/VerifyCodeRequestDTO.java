package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class VerifyCodeRequestDTO {

    @NotBlank(message = "Contact (email yoki telefon) bo‘sh bo‘lmasligi kerak")
    private String contact;

    @NotBlank(message = "Verification code bo‘sh bo‘lmasligi kerak")
    @Size(min = 6, max = 6, message = "Kod 6 xonali bo‘lishi kerak")
    private String code;
}
