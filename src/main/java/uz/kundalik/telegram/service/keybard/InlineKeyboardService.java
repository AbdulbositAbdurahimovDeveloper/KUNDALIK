package uz.kundalik.telegram.service.keybard;

import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import uz.kundalik.site.payload.response.PageDTO;

import java.util.List;

public interface InlineKeyboardService {
    InlineKeyboardMarkup welcomeFirstTime(Long userChatId);

    InlineKeyboardButton createButton(String text, String callbackData);

    List<InlineKeyboardButton> createPaginationRow(Page<?> page, String baseCallback);

    List<InlineKeyboardButton> createPaginationRow(PageDTO<?> page, String baseCallback);

    void addPaginationButtons(List<List<InlineKeyboardButton>> keyboard, Page<?> page, String baseCallback);
}
