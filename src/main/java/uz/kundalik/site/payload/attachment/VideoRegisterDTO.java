package uz.kundalik.site.payload.attachment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class VideoRegisterDTO {

    @NotBlank(message = "YouTube URL cannot be blank.")
    @URL(message = "Please provide a valid URL.")
    // Qo'shimcha, kuchliroq validatsiya uchun pattern qo'shish mumkin
    // @Pattern(regexp = "^(https?://)?(www\\.youtube\\.com|youtu\\.be)/.+", message = "Please provide a valid YouTube URL.")
    private String youtubeUrl;

    @NotBlank(message = "Video title cannot be blank.")
    @Size(max = 255, message = "Title cannot exceed 255 characters.")
    private String title;
}