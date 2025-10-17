package uz.kundalik.site.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.kundalik.site.model.User;
import uz.kundalik.site.payload.response.MessageResponseDTO;
import uz.kundalik.site.payload.user.*;
import uz.kundalik.site.service.template.UserService;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // --- Profile Management ---

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> getCurrentUserProfile(@AuthenticationPrincipal User currentUser) {
        UserResponseDTO userResponseDTO = userService.getCurrentUserProfile(currentUser);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PatchMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDTO> updateCurrentUserProfile(@Valid @RequestBody UserUpdateRequestDTO requestDTO,
                                                                    @AuthenticationPrincipal User currentUser) {
        UserResponseDTO userResponseDTO = userService.updateCurrentUserProfile(requestDTO, currentUser);
        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDTO> uploadProfilePicture(@RequestParam("file") MultipartFile file,
                                                                   @AuthenticationPrincipal User currentUser) {
        MessageResponseDTO messageResponseDTO = userService.uploadProfilePicture(file, currentUser);
        return ResponseEntity.ok(messageResponseDTO);
    }

    // --- Security Management ---

    @PostMapping("/change-password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDTO> changePassword(@Valid @RequestBody ChangePasswordDTO requestDTO,
                                                             @AuthenticationPrincipal User currentUser) {
        MessageResponseDTO messageResponseDTO = userService.changePassword(requestDTO, currentUser);
        return ResponseEntity.ok(messageResponseDTO);
    }

    @PostMapping("/change-email-request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDTO> requestEmailChange(@Valid @RequestBody EmailChangeRequestDTO requestDTO,
                                                                 @AuthenticationPrincipal User currentUser) {
        MessageResponseDTO messageResponseDTO = userService.requestEmailChange(requestDTO, currentUser);
        return ResponseEntity.ok(messageResponseDTO);
    }


    @PostMapping("/resend-email-verification")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponseDTO> resendEmailVerification(@AuthenticationPrincipal User currentUser) {
        MessageResponseDTO messageResponseDTO = userService.resendEmailVerification(currentUser);
        return ResponseEntity.ok(messageResponseDTO);
    }


    // --- Account Deletion ---

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteMyAccount(@AuthenticationPrincipal User currentUser) {
        userService.deleteMyAccount(currentUser);
        return ResponseEntity.noContent().build();
    }
}