package uz.kundalik.telegram.controller;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.kundalik.site.enums.Role;
import uz.kundalik.telegram.enums.UserState;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.model.TelegramUser;
import uz.kundalik.telegram.repository.TelegramUserRepository;
import uz.kundalik.telegram.service.*;
import uz.kundalik.telegram.service.redirect.RedirectService;
import uz.kundalik.telegram.utils.Utils;

import java.util.Objects;

@Component
public class UpdateDispatcherService {

    private final RoleService roleService;
    private final TelegramUserRepository telegramUserRepository;
    private final UserStateService userStateService;
    private final RedirectService redirectService;


    public UpdateDispatcherService(@Lazy RoleService roleService,
                                   @Lazy TelegramUserRepository telegramUserRepository,
                                   @Lazy UserStateService userStateService,
                                   @Lazy RedirectService redirectService) {
        this.roleService = roleService;
        this.telegramUserRepository = telegramUserRepository;
        this.userStateService = userStateService;
        this.redirectService = redirectService;
    }

    @Transactional
    @Async
    public void dispatch(Update update) {

        Long userChatId = getUserChatId(update);

        TelegramUser currentUser = telegramUserRepository.findByChatId(userChatId)
                .orElse(null);

        if (Objects.isNull(currentUser)) {

            TelegramUser telegramUser = new TelegramUser();
            telegramUser.setChatId(userChatId);
            telegramUser.setUserState(UserState.AUTHENTICATED);
            telegramUser.setUserStatus(UserStatus.ANONYMOUS);
            telegramUserRepository.save(telegramUser);
            telegramUserRepository.flush();

        }

        Long chatId = getUserChatId(update);
        Role currentRole = roleService.getUserRole(chatId);

        if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().equals(Utils.START)) {
            userStateService.updateUserState(chatId, UserState.DEFAULT);
        }

        if (Objects.equals(currentRole, Role.ADMIN)) {

            redirectService.adminRedirect(update);

        } else {

            redirectService.userRedirect(update);

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
