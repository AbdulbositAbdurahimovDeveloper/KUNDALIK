package uz.kundalik.site.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.kundalik.site.model.User;
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.site.payload.response.ResponseDTO;
import uz.kundalik.site.payload.user.AssignRoleRequestDTO;
import uz.kundalik.site.payload.user.UserListItemDTO;
import uz.kundalik.site.payload.user.UserResponseDTO;
import uz.kundalik.site.payload.user.UserStatusUpdateDTO;
import uz.kundalik.site.service.template.AdminUserService;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER')")
    public ResponseEntity<ResponseDTO<PageDTO<UserListItemDTO>>> getAllUsers(@RequestParam(defaultValue = "0") Integer page,
                                                                             @RequestParam(defaultValue = "10") Integer size) {
        // SUPER_ADMIN, ADMIN va MANAGER'lar foydalanuvchilar ro'yxatini ko'ra oladi.
        PageDTO<UserListItemDTO> userListItemDTOS = adminUserService.getAllUsers(page, size);
        return ResponseEntity.ok(ResponseDTO.success(userListItemDTOS));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'MANAGER', 'OPERATOR')")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> getUserById(@PathVariable Long id) {
        // Yuqoridagilarga qo'shimcha ravishda, OPERATOR ham alohida foydalanuvchini (masalan, yordam berish uchun) ko'ra olishi mumkin.
        UserResponseDTO userResponseDTO = adminUserService.getUserById(id);
        return ResponseEntity.ok(ResponseDTO.success(userResponseDTO));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> updateUserStatus(@PathVariable Long id, @RequestBody UserStatusUpdateDTO statusDTO,
                                                                         @AuthenticationPrincipal User currentUser) {
        // Faqat yuqori darajadagi adminlar foydalanuvchini bloklashi yoki faollashtirishi mumkin. MANAGER bunga haqli emas.
        UserResponseDTO userResponseDTO = adminUserService.updateUserStatus(id, statusDTO,currentUser);
        return ResponseEntity.ok(ResponseDTO.success(userResponseDTO));
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ResponseDTO<UserResponseDTO>> assignUserRole(@PathVariable Long id, @RequestBody AssignRoleRequestDTO roleDTO,
                                                                       @AuthenticationPrincipal User currentUser) {
        // Rollarni faqat SUPER_ADMIN va ADMIN o'zgartira oladi.
        // **Muhim:** Service qatlamida qo'shimcha tekshiruv bo'lishi kerak!
        UserResponseDTO userResponseDTO = adminUserService.assignUserRole(id, roleDTO,currentUser);
        return ResponseEntity.ok(ResponseDTO.success(userResponseDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ResponseDTO<Void>> deleteUser(@PathVariable Long id,@AuthenticationPrincipal User currentUser) {
        // Foydalanuvchini tizimdan butunlay o'chirish kabi xavfli operatsiyani faqat SUPER_ADMIN bajara oladi.
        adminUserService.deleteUser(id,currentUser);
        return ResponseEntity.ok().build();
    }
}