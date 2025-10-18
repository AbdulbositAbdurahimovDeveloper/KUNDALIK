package uz.kundalik.telegram.service.panel.user;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramUserService {

    void onUpdateResave(Update update);

}
