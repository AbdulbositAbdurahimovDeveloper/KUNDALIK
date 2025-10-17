package uz.kundalik.telegram.service.keybard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.List;

@Service
public class InlineKeyboardServiceImpl implements InlineKeyboardService {

    @Value("${telegram.bot.webhook-path}")
    private String webApp;

    @Override
    public InlineKeyboardMarkup welcomeFirstTime(Long userChatId) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton button = new InlineKeyboardButton("🚀 Kirish / Ro'yxatdan o'tish");
//        button.setUrl(appDomain + "/auth.html?chat_id=" + chatId);

        // bu yerda miniappni ochib berishimiz kerak
        button.setWebApp(new WebAppInfo(webApp + "/tg-bot-mini-app/auth.html"));

        inlineKeyboardMarkup.setKeyboard(List.of(List.of(button)));
        return inlineKeyboardMarkup;

    }
}
