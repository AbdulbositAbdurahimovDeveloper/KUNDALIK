//package uz.kundalik.site.validation.annotations;
//
//import jakarta.validation.Constraint;
//import jakarta.validation.Payload;
//import uz.kundalik.site.validation.validators.RegistrationRequestValidator;
//
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
///**
// * Custom validation annotation to ensure that a user registration request is valid.
// * It checks that:
// * 1. If verification method is EMAIL, the email field must not be blank.
// * 2. If verification method is PHONE, the phoneNumber field must not be blank.
// *
// * This annotation should be applied at the class level to the DTO.
// */
//@Constraint(validatedBy = RegistrationRequestValidator.class)
//@Target({ ElementType.TYPE })
//@Retention(RetentionPolicy.RUNTIME)
//public @interface ValidRegistrationRequest {
//
//    String message() default "Invalid registration data. Please check the provided email, phone number, and verification method.";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//}