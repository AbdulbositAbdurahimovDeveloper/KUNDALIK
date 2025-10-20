package uz.kundalik.site.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.kundalik.site.payload.request.*;
import uz.kundalik.site.payload.response.MessageResponseDTO;
import uz.kundalik.site.payload.response.ResponseDTO;
import uz.kundalik.site.payload.response.TokenDTO;
import uz.kundalik.site.payload.user.ForgotPasswordRequestDTO;
import uz.kundalik.site.payload.user.UserRegisterRequestDTO;
import uz.kundalik.site.payload.user.UserRegisterResponseDTO;
import uz.kundalik.site.payload.user.VerifyCodeRequestDTO;
import uz.kundalik.site.service.template.AuthService;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns user details along with an access token."
    )
    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserRegisterResponseDTO>> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Data required for user registration.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Standard User Registration",
                                            summary = "Example request for new user",
                                            value = """
                                                    {
                                                      "email": "abdulbositabdurahimov@gmail.com",
                                                      "password": "new_parol",
                                                      "firstName": "Abdulbosit",
                                                      "lastName": "Abdurahimov",
                                                      "birthDate": "2006-03-25",
                                                      "gender": "MALE"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "User Registration",
                                            summary = "Example request for user",
                                            value = """
                                                    {
                                                      "email": "abdulbositabdurahimov260@gmail.com",
                                                      "password": "1234",
                                                      "firstName": "Abdulbosit",
                                                      "lastName": "Abdurahimov",
                                                      "birthDate": "2006-03-25",
                                                      "gender": "MALE"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @Valid @RequestBody UserRegisterRequestDTO requestDTO) {

        UserRegisterResponseDTO userRegisterResponseDTO = authService.registerUser(requestDTO);
        return ResponseEntity.ok(ResponseDTO.success(userRegisterResponseDTO));
    }
    // --- Registration ---
//    @PostMapping("/register")
//    public ResponseEntity<ResponseDTO<UserRegisterResponseDTO>> registerUser(
//            @Valid @RequestBody UserRegisterRequestDTO requestDTO) {
//        UserRegisterResponseDTO userRegisterResponseDTO = authService.registerUser(requestDTO);
//        return ResponseEntity.ok(ResponseDTO.success(userRegisterResponseDTO));
//    }

    // --- Verification (email yoki phone kodni tekshirish) ---
    @PostMapping("/verify")
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> verifyCode(
            @Valid @RequestBody VerifyCodeRequestDTO requestDTO) {
        MessageResponseDTO responseDTO = authService.verifyUserCode(requestDTO);
        return ResponseEntity.ok(ResponseDTO.success(responseDTO));
    }

    // --- Authentication ---
    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<TokenDTO>> loginUser(@Valid @RequestBody LoginDTO loginDTO) {
        TokenDTO tokenDTO = authService.loginUser(loginDTO);
        return ResponseEntity.ok(ResponseDTO.success(tokenDTO));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ResponseDTO<TokenDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO requestDTO) {
        TokenDTO tokenDTO = authService.refreshToken(requestDTO);
        return ResponseEntity.ok(ResponseDTO.success(tokenDTO));
    }

    // --- Password Reset ---
    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO requestDTO) {
        MessageResponseDTO messageResponseDTO = authService.initiatePasswordReset(requestDTO);
        return ResponseEntity.ok(ResponseDTO.success(messageResponseDTO));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> resetPassword(
            @RequestParam("token") String token,
            @Valid @RequestBody ResetPasswordRequestDTO requestDTO) {
        MessageResponseDTO messageResponseDTO = authService.completePasswordReset(token, requestDTO);
        return ResponseEntity.ok(ResponseDTO.success(messageResponseDTO));
    }

    @GetMapping("/confirm-email-change")
    public ResponseEntity<Void> confirmEmailChange(@RequestParam("token") String token,
                                                   @RequestParam("redirect_url") String redirectUrl) {
        String finalRedirectUrl = authService.processEmailChangeConfirmation(token, redirectUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(finalRedirectUrl))
                .build();
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ResponseDTO<MessageResponseDTO>> resendInitialVerification(
            @Valid @RequestBody ResendVerificationRequestDTO requestDTO) {
        MessageResponseDTO messageResponseDTO = authService.resendInitialVerification(requestDTO);
        return ResponseEntity.ok(ResponseDTO.success(messageResponseDTO));
    }

}
