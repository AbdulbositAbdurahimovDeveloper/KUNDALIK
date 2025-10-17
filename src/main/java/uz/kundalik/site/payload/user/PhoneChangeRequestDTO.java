package uz.kundalik.site.payload.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneChangeRequestDTO {

    @NotBlank(message = "Yangi telefon raqami bo'sh bo'lishi mumkin emas")
    @Pattern(regexp = "^\\+998[0-9]{9}$", message = "Telefon raqam formati noto'g'ri (Masalan: +998901234567)")
    private String newPhoneNumber;

    @NotBlank(message = "Current password is required to confirm your identity")
    private String currentPassword;
}