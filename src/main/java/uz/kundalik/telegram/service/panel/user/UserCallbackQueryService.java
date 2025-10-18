package uz.kundalik.telegram.service.panel.user;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface UserCallbackQueryService {
    void handleCallbackQuery(CallbackQuery callbackQuery);
}
