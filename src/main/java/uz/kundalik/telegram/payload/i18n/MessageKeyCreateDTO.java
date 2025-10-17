package uz.kundalik.telegram.payload.i18n;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageKeyCreateDTO {

    @NotBlank(message = "Key cannot be blank")
    @Size(max = 100, message = "Key cannot be longer than 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Key can only contain letters, numbers, dots, underscores, and hyphens")
    private String key; // Masalan: "button.save", "error.user-not-found"

    private String description;
}