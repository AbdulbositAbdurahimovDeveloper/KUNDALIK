package uz.kundalik.telegram.service.keybard;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface InlineKeyboardService {
    InlineKeyboardMarkup welcomeFirstTime(Long userChatId);

}
