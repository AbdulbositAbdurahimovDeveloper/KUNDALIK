package uz.kundalik.site.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uz.kundalik.site.payload.response.errors.ErrorDTO;

import java.io.Serial;
import java.io.Serializable;

/**
 * A generic and standardized response wrapper for all API endpoints.
 * <p>
 * This class ensures that every response from the API has a consistent structure. It is designed
 * to be created exclusively via its static factory methods ({@code success()} and {@code error()}),
 * providing a clear and controlled way to build responses.
 *
 * @param <T> The type of the data payload being returned on success.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * A boolean flag indicating the outcome of the request.
     * {@code true} for a successful response, {@code false} for an error. This should be the
     * primary field clients check to determine how to process the response.
     */
    private boolean success;

    /**
     * A human-readable message providing context about the response.
     * For successful responses, it might be a simple "Success". For errors, it typically
     * mirrors the main error message from the {@link ErrorDTO}.
     */
    private String message;

    /**
     * The actual data payload for successful responses.
     * This field will be {@code null} and thus omitted from the JSON output for error responses.
     */
    private T data;

    /**
     * A detailed error object for failed responses.
     * This field will be {@code null} and thus omitted from the JSON output for successful responses.
     */
    private ErrorDTO error;

    // --- Static Factory Methods ---

    /**
     * Creates a successful response DTO with a custom message and data payload.
     *
     * @param data    The data payload to be returned to the client.
     * @param message A descriptive message about the successful operation.
     * @param <T>     The generic type of the data payload.
     * @return A {@link ResponseDTO} instance representing a successful outcome.
     */
    public static <T> ResponseDTO<T> success(T data, String message) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.success = true;
        response.message = message;
        response.data = data;
        return response;
    }

    /**
     * Creates a successful response DTO with a default "Success" message.
     *
     * @param data The data payload to be returned.
     * @param <T>  The generic type of the data payload.
     * @return A {@link ResponseDTO} instance representing a successful outcome.
     */
    public static <T> ResponseDTO<T> success(T data) {
        return success(data, "Success");
    }

    /**
     * Creates an error response DTO from a detailed {@link ErrorDTO} object.
     * The `success` flag is automatically set to {@code false}.
     *
     * @param error The detailed error object.
     * @param <T>   The generic type parameter, which will be {@link Void} for error responses.
     * @return A {@link ResponseDTO} instance representing a failed outcome.
     */
    public static <T> ResponseDTO<T> error(ErrorDTO error) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.success = false;
        response.message = error.message(); // Mirror the main error message
        response.error = error;
        return response;
    }
}