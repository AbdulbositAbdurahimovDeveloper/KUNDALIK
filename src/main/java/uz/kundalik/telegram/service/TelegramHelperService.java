package uz.kundalik.telegram.service;

import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.site.model.User;
import uz.kundalik.telegram.enums.UserState;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.model.Language;
import uz.kundalik.telegram.model.TelegramUser;

public interface TelegramHelperService {

    TelegramUser telegramUser(Long chatId);

    Language userLanguage(Long chatId);

    String langCode(Long chatId);

    UserState userState(Long chatId);

    User user(Long chatId);

    @Transactional(readOnly = true)
    UserStatus userStatus(Long chatId);
}
