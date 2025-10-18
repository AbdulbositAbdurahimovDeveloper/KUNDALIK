package uz.kundalik.telegram.service.keybard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import uz.kundalik.site.payload.response.PageDTO;
import uz.kundalik.telegram.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static uz.kundalik.telegram.utils.Utils.Action.*;

@Service
public class InlineKeyboardServiceImpl implements InlineKeyboardService {


    @Value("${application.telegram.bot.webhook-path}")
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

    @Override
    public InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
    }


    @Override
    public List<InlineKeyboardButton> createPaginationRow(Page<?> page, String baseCallback) {
        List<InlineKeyboardButton> paginationRow = new ArrayList<>();

        if (page.getTotalPages() <= 1) {
            return paginationRow;
        }

        int currentPage = page.getNumber();

        if (page.hasPrevious()) {
            String prevCallback = String.join(":",
                    baseCallback,
                    ACTION_PAGE,
                    String.valueOf(currentPage - 1)
            );
            paginationRow.add(createButton(Utils.InlineButtons.PAGINATION_PREVIOUS_TEXT, prevCallback));
        }

        String pageIndicator = String.format("%d / %d", currentPage + 1, page.getTotalPages());
        paginationRow.add(createButton(pageIndicator, "do_nothing"));

        if (page.hasNext()) {
            String nextCallback = String.join(":",
                    baseCallback,
                    ACTION_PAGE,
                    String.valueOf(currentPage + 1)
            );
            paginationRow.add(createButton(Utils.InlineButtons.PAGINATION_NEXT_TEXT, nextCallback));
        }
        return paginationRow;
    }

    @Override
    public List<InlineKeyboardButton> createPaginationRow(PageDTO<?> page, String baseCallback) {
        List<InlineKeyboardButton> paginationRow = new ArrayList<>();

        if (page.getTotalPages() <= 1) {
            return paginationRow;
        }

        int currentPage = page.getPageNumber();

        if (!page.isFirst()) {
            String prevCallback = String.join(":",
                    baseCallback,
                    ACTION_PAGE,
                    String.valueOf(currentPage - 1)
            );
            paginationRow.add(createButton(Utils.InlineButtons.PAGINATION_PREVIOUS_TEXT, prevCallback));
        }

        String pageIndicator = String.format("%d / %d", currentPage + 1, page.getTotalPages());
        paginationRow.add(createButton(pageIndicator, "do_nothing")); // Bosilganda hech nima qilmaydi

        if (!page.isLast()) {
            String nextCallback = String.join(":",
                    baseCallback,
                    ACTION_PAGE,
                    String.valueOf(currentPage + 1)
            );
            paginationRow.add(createButton(Utils.InlineButtons.PAGINATION_NEXT_TEXT, nextCallback));
        }

        return paginationRow;
    }

    @Override
    public void addPaginationButtons(List<List<InlineKeyboardButton>> keyboard, Page<?> page, String baseCallback) {
        if (page.getTotalPages() > 1) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            int currentPage = page.getNumber();

            if (page.hasPrevious()) {
                String prevCallback = String.join(":",
                        baseCallback, ACTION_LIST, ACTION_PAGE, String.valueOf(currentPage - 1));
                row.add(createButton("⬅️ Oldingi", prevCallback));
            }

            row.add(createButton(String.format("%d / %d", currentPage + 1, page.getTotalPages()), "do_nothing"));

            if (page.hasNext()) {
                String nextCallback = String.join(":",
                        baseCallback, ACTION_LIST, ACTION_PAGE, String.valueOf(currentPage + 1));
                row.add(createButton("Keyingi ➡️", nextCallback));
            }
            keyboard.add(row);
        }
    }
}
