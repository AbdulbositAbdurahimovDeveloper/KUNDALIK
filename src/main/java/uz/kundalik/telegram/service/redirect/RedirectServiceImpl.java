package uz.kundalik.telegram.service.redirect;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.kundalik.telegram.service.panel.admin.AdminCallbackQueryService;
import uz.kundalik.telegram.service.panel.admin.AdminProcessMessageService;
import uz.kundalik.telegram.service.panel.user.UserCallbackQueryService;
import uz.kundalik.telegram.service.panel.user.UserProcessMessageService;

@Service
@RequiredArgsConstructor
public class RedirectServiceImpl implements  RedirectService {

    private final AdminCallbackQueryService adminCallbackQueryService;
    private final AdminProcessMessageService adminProcessMessageService;
    private final UserCallbackQueryService userCallbackQueryService;
    private final UserProcessMessageService userProcessMessageService;

    @Override
    public void adminRedirect(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            adminCallbackQueryService.handleCallbackQuery(callbackQuery);
        } else if (update.hasMessage()) {
            adminProcessMessageService.processMessage(update.getMessage());
        }

    }

    @Override
    public void userRedirect(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            userCallbackQueryService.handleCallbackQuery(callbackQuery);
        } else if (update.hasMessage()) {
            userProcessMessageService.processMessage(update.getMessage());
        }

    }
}
