package uz.kundalik.site.payload.response.errors;

/**
 * An immutable Data Transfer Object representing a single validation error on a specific request field.
 * <p>
 * This record is used within the {@link ErrorDTO} to provide detailed feedback on which
 * part of the request payload failed validation and why.
 *
 * @param field   The name of the field that failed validation (e.g., "username", "email").
 * @param message A descriptive message explaining the validation rule that was violated
 *                (e.g., "Username cannot be blank", "Email should be valid").
 */
public record FieldErrorDTO(String field, String message) {
}