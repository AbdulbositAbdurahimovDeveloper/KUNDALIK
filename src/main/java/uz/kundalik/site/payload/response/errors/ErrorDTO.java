package uz.kundalik.site.payload.response.errors;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.UUID;

/**
 * An immutable Data Transfer Object representing detailed error information.
 * <p>
 * This record is used to provide a structured error response to the client. It includes
 * an HTTP status code, a human-readable message, a unique trace ID for logging, and an
 * optional list of field-specific errors for validation failures.
 *
 * @param status      The HTTP status code associated with the error (e.g., 400, 404, 500).
 * @param message     A clear, concise, and human-readable description of the error.
 * @param traceId     A unique identifier for this specific error instance, useful for correlating client-side
 *                    errors with server-side logs.
 * @param fieldErrors An optional list of {@link FieldErrorDTO}s, typically used for
 *                    request validation failures (HTTP 400). It will be {@code null}
 *                    for other types of errors.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDTO(
        int status,
        String message,
        String traceId,
        List<FieldErrorDTO> fieldErrors
) {
    /**
     * A convenience constructor for creating a simple error without field-specific details.
     * Automatically generates a unique {@code traceId}.
     *
     * @param status  The HTTP status code.
     * @param message The error message.
     */
    public ErrorDTO(int status, String message) {
        this(status, message, UUID.randomUUID().toString(), null);
    }

    /**
     * A convenience constructor for creating a validation error with field-specific details.
     * Automatically generates a unique {@code traceId}.
     *
     * @param status      The HTTP status code (typically 400).
     * @param message     A general message for the validation failure.
     * @param fieldErrors A list of specific field validation errors.
     */
    public ErrorDTO(int status, String message, List<FieldErrorDTO> fieldErrors) {
        this(status, message, UUID.randomUUID().toString(), fieldErrors);
    }
}