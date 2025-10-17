package uz.kundalik.site.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A generic response DTO for returning simple, user-friendly messages from the API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {
    private String message;
}