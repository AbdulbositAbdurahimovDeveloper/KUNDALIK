//package uz.kundalik.site.validation.validators;
//
//import jakarta.validation.ConstraintValidator;
//import jakarta.validation.ConstraintValidatorContext;
//import uz.kundalik.site.enums.VerificationMethod;
//import uz.kundalik.site.payload.user.UserRegisterRequestDTO;
//import uz.kundalik.site.validation.annotations.ValidRegistrationRequest;
//
//public class RegistrationRequestValidator implements ConstraintValidator<ValidRegistrationRequest, UserRegisterRequestDTO> {
//
//    @Override
//    public boolean isValid(UserRegisterRequestDTO request, ConstraintValidatorContext context) {
//        if (request == null) {
//            return true; // Let other annotations handle null objects
//        }
//
//        VerificationMethod method = request.getVerificationMethod();
//        String email = request.getEmail();
//        String phoneNumber = request.getPhoneNumber();
//
//        // One of them must be provided
//        if ((email == null || email.isBlank()) && (phoneNumber == null || phoneNumber.isBlank())) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("Either email or phone number must be provided.")
//                   .addPropertyNode("email") // You can point to a specific field
//                   .addConstraintViolation();
//            return false;
//        }
//
//        // Check consistency with the verification method
//        if (method == VerificationMethod.EMAIL && (email == null || email.isBlank())) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("Email must be provided when verification method is EMAIL.")
//                   .addPropertyNode("email")
//                   .addConstraintViolation();
//            return false;
//        }
//
//        if (method == VerificationMethod.PHONE && (phoneNumber == null || phoneNumber.isBlank())) {
//            context.disableDefaultConstraintViolation();
//            context.buildConstraintViolationWithTemplate("Phone number must be provided when verification method is PHONE.")
//                   .addPropertyNode("phoneNumber")
//                   .addConstraintViolation();
//            return false;
//        }
//
//        return true;
//    }
//}