package uz.kundalik.telegram.controller;

import jakarta.validation.Valid;
import jakarta.ws.rs.HEAD;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import uz.kundalik.site.exception.EntityNotFoundException;
import uz.kundalik.site.model.User;
import uz.kundalik.site.payload.request.LoginDTO;
import uz.kundalik.site.payload.response.ResponseDTO;
import uz.kundalik.site.payload.response.TokenDTO;
import uz.kundalik.site.payload.response.errors.ErrorDTO;
import uz.kundalik.site.payload.user.UserRegisterRequestDTO;
import uz.kundalik.site.payload.user.UserRegisterResponseDTO;
import uz.kundalik.site.repository.UserRepository;
import uz.kundalik.site.service.template.AuthService;
import uz.kundalik.site.service.template.UserService;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.model.TelegramUser;
import uz.kundalik.telegram.repository.TelegramUserRepository;
import uz.kundalik.telegram.service.TelegramValidationService;

@RestController
@RequestMapping("/api/public/telegram")
public class TelegramWebhookController {

    private final RestTemplate restTemplate = new RestTemplate();
    private final TelegramValidationService telegramValidationService;
    private final AuthService authService;
    private final UserService userService;
    private final TelegramUserRepository telegramUserRepository;
    private final UserRepository userRepository;


    @Value("${application.telegram.bot.token}")
    private String botToken;

    public TelegramWebhookController(
            @Lazy TelegramValidationService telegramValidationService,
            @Lazy AuthService authService, UserService userService,
            @Lazy TelegramUserRepository telegramUserRepository,
            @Lazy UserRepository userRepository) {
        this.telegramValidationService = telegramValidationService;
        this.authService = authService;
        this.userService = userService;
        this.telegramUserRepository = telegramUserRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/set-webhook")
    public ResponseEntity<String> setWebhook(@RequestParam String domain) {
        String webhookUrl = domain.replaceAll("/+$", "") + "/telegram-bot";
        String url = "https://api.telegram.org/bot" + botToken + "/setWebhook?url=" + webhookUrl;

        String response = restTemplate.getForObject(url, String.class);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO,
                                  @RequestHeader(value = "Telegram-Init-Data", required = false) String initData) {

        if (telegramValidationService.validate(initData)) {

            TokenDTO tokenDTO = authService.loginUser(loginDTO);

            String email = tokenDTO.getEmail();

            User user = (User) userService.loadUserByUsername(email);

            Long chatId = telegramValidationService.getUserId(initData);

            TelegramUser telegramUser = telegramUserRepository.findByChatId(chatId)
                    .orElseThrow(() -> new EntityNotFoundException("Telegram user not found"));
            telegramUser.setSiteUser(user);
            telegramUser.setUserStatus(UserStatus.REGISTERED);

            user.setTelegramUser(telegramUser);
            userRepository.save(user);
            return ResponseEntity.ok(ResponseDTO.success("OK"));
        }

        return ResponseEntity.status(403).body(ResponseDTO.error(new ErrorDTO(403, "Xavfsizlik tekshiruvidan o'tmadi!")));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterRequestDTO registerDTO,
                                      @RequestHeader(value = "Telegram-Init-Data", required = false) String initData) {

        if (telegramValidationService.validate(initData)) {

            UserRegisterResponseDTO registerUser = authService.registerUser(registerDTO);
            User user = userRepository.findById(registerUser.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            Long chatId = telegramValidationService.getUserId(initData);

            TelegramUser telegramUser = telegramUserRepository.findByChatId(chatId)
                    .orElseThrow(() -> new EntityNotFoundException("Telegram user not found"));

            if (telegramUser.getSiteUser() != null) {
                return ResponseEntity.status(409).body(ResponseDTO.error(new ErrorDTO(409, "Telegram user already connected")));
            }

            user.setTelegramUser(telegramUser);
            userRepository.save(user);

            return ResponseEntity.ok(ResponseDTO.success("OK"));

        }
        return ResponseEntity.status(403).body(ResponseDTO.error(new ErrorDTO(403, "Xavfsizlik tekshiruvidan o'tmadi!")));
    }
}
