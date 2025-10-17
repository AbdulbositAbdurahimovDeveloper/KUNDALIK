package uz.kundalik.site.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.kundalik.site.validation.validators.FieldMatchValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom validation annotation to verify that two fields in a DTO are equal.
 * Useful for "password" and "confirmPassword" fields.
 */
@Constraint(validatedBy = FieldMatchValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldMatch {
    String message() default "The fields must match";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String first();
    String second();
}