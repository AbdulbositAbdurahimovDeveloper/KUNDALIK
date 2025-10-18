package uz.kundalik.telegram.service.panel.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@RequiredArgsConstructor
public class AdminCallbackQueryServiceImpl implements  AdminCallbackQueryService {
    @Override
    public void handleCallbackQuery(CallbackQuery callbackQuery) {

    }
}
