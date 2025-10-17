package uz.kundalik.telegram.payload.i18n;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LanguageUpdateDTO {

    @NotBlank(message = "Language name cannot be blank")
    @Size(max = 100, message = "Language name cannot be longer than 100 characters")
    private String name;

    @NotNull(message = "Active status cannot be null")
    private Boolean active;

}