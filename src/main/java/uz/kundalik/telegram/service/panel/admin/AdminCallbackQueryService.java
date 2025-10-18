package uz.kundalik.telegram.service.panel.admin;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface AdminCallbackQueryService {
    void handleCallbackQuery(CallbackQuery callbackQuery);

}
