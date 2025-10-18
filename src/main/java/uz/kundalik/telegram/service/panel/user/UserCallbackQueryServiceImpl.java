package uz.kundalik.telegram.service.panel.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import uz.kundalik.telegram.service.panel.admin.AdminCallbackQueryService;

@Service
@RequiredArgsConstructor
public class UserCallbackQueryServiceImpl implements UserCallbackQueryService {
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {

    }
}
