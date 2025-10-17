package uz.kundalik.telegram.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import uz.kundalik.telegram.enums.UserState;

public interface TelegramUserService {
    void updateUserState(Long chatId, UserState userState);

    void onUpdateResave(Update update);

}
