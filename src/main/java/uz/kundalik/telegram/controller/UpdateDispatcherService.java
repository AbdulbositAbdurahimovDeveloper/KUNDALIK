package uz.kundalik.telegram.controller;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.kundalik.site.enums.Role;
import uz.kundalik.telegram.enums.UserState;
import uz.kundalik.telegram.model.TelegramUser;
import uz.kundalik.telegram.repository.TelegramUserRepository;
import uz.kundalik.telegram.service.*;
import uz.kundalik.telegram.service.keybard.InlineKeyboardService;
import uz.kundalik.telegram.service.message.SendMsg;
import uz.kundalik.telegram.utils.Utils;

import java.util.Objects;

@Component
public class UpdateDispatcherService {

    private final SendMsg sendMsg;
    private final RoleService roleService;
    private final KundalikBot kundalikBot;
    private final TelegramUserService telegramUserService;
    private final TelegramAdminService telegramAdminService;
    private final TelegramUserRepository telegramUserRepository;
    private final InlineKeyboardService inlineKeyboardService;


    public UpdateDispatcherService(@Lazy SendMsg sendMsg,
                                   @Lazy RoleService roleService,
                                   @Lazy TelegramUserService telegramUserService,
                                   @Lazy KundalikBot kundalikBot,
                                   @Lazy TelegramAdminService telegramAdminService,
                                   @Lazy TelegramUserRepository telegramUserRepository,
                                   @Lazy InlineKeyboardService inlineKeyboardService) {
        this.sendMsg = sendMsg;
        this.roleService = roleService;
        this.telegramUserService = telegramUserService;
        this.kundalikBot = kundalikBot;
        this.telegramAdminService = telegramAdminService;
        this.telegramUserRepository = telegramUserRepository;
        this.inlineKeyboardService = inlineKeyboardService;
    }

    @Transactional
    @Async
    public void dispatch(Update update) {

        Long userChatId = getUserChatId(update);

        TelegramUser currentUser = telegramUserRepository.findByChatId(userChatId)
                .orElse(null);

        if (Objects.isNull(currentUser) || Objects.isNull(currentUser.getSiteUser())) {

            TelegramUser telegramUser = new TelegramUser();
            telegramUser.setChatId(userChatId);
            telegramUser.setUserState(UserState.AUTHENTICATED);
            telegramUserRepository.save(telegramUser);
            telegramUserRepository.flush();

            String sendMessage = """
                    Assalomu alaykum! Botimizga xush kelibsiz.\s
                    
                    Iltimos, ro'yxatdan o'tish uchun quyidagi tugmani bosing.""";
            InlineKeyboardMarkup inlineKeyboardMarkup = inlineKeyboardService.welcomeFirstTime(userChatId);
            kundalikBot.myExecute(sendMsg.sendMessage(userChatId, sendMessage, inlineKeyboardMarkup));
            return;

        }

        Long chatId = getUserChatId(update);
        Role currentRole = roleService.getUserRole(chatId);

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(Utils.START)) {
            telegramUserService.updateUserState(chatId, UserState.DEFAULT);
        }

        if (Objects.equals(currentRole, Role.ADMIN)) {

            telegramAdminService.onUpdateResave(update);

        } else if (Objects.equals(currentRole, Role.USER)) {

            telegramUserService.onUpdateResave(update);

        }
    }


    private Long getUserChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasMyChatMember()) {
            return update.getMyChatMember().getChat().getId();
        }
        return null;
    }
}
