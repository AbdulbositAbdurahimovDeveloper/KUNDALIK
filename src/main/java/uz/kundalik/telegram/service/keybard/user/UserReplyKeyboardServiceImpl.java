package uz.kundalik.telegram.service.keybard.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.service.TelegramHelperService;
import uz.kundalik.telegram.service.message.i18n;
import uz.kundalik.telegram.utils.Utils;
import uz.kundalik.telegram.utils.Utils.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserReplyKeyboardServiceImpl implements UserReplyKeyboardService {

    private final i18n i18n;
    private final TelegramHelperService telegramHelperService;

    @Override
    public ReplyKeyboardMarkup welcomeMsg(String langCode, UserStatus userStatus) {

        ReplyKeyboardMarkup replyKeyboardMarkup = createBaseReplyKeyboard();
        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();

        row1.add(i18n.get(Utils.i18n.BUTTON_WEATHER, langCode));
        row1.add(i18n.get(Utils.i18n.BUTTON_PRAYER, langCode));
        row2.add(i18n.get(Utils.i18n.BUTTON_CURRENCY, langCode));

        switch (userStatus) {
            case ANONYMOUS -> {
                row2.add(Action.LOCK + i18n.get(Utils.i18n.BUTTON_NOTES, langCode));
                row3.add(Action.LOCK + i18n.get(Utils.i18n.BUTTON_REMINDER, langCode));
                row3.add(Action.LOCK + i18n.get(Utils.i18n.BUTTON_BIRTHDATE, langCode));

                row4.add(Action.LOCK + i18n.get(Utils.i18n.BUTTON_WALLET, langCode));
                row4.add(Action.LOCK + i18n.get(Utils.i18n.BUTTON_SETTINGS, langCode));
            }
            case REGISTERED -> {
                row2.add(i18n.get(Utils.i18n.BUTTON_NOTES, langCode));
                row2.add(i18n.get(Utils.i18n.BUTTON_REMINDER, langCode));
                row2.add(i18n.get(Utils.i18n.BUTTON_BIRTHDATE, langCode));

                row3.add(Action.LOCK + i18n.get(Utils.i18n.BUTTON_WALLET, langCode));
                row3.add(i18n.get(Utils.i18n.BUTTON_SETTINGS, langCode));
            }
            case PREMIUM -> {
                row2.add(i18n.get(Utils.i18n.BUTTON_NOTES, langCode));
                row2.add(i18n.get(Utils.i18n.BUTTON_REMINDER, langCode));
                row2.add(i18n.get(Utils.i18n.BUTTON_BIRTHDATE, langCode));

                row3.add(i18n.get(Utils.i18n.BUTTON_WALLET, langCode));
                row3.add(i18n.get(Utils.i18n.BUTTON_SETTINGS, langCode));
            }
            default -> {
                log.warn("userStatus not found");
            }
        }


        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);
        keyboardRows.add(row4);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
        return replyKeyboardMarkup;

    }

    @Override
    public ReplyKeyboardMarkup getLocationMenu(String langCode) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        replyKeyboardMarkup.setInputFieldPlaceholder("send_location");
        replyKeyboardMarkup.setSelective(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        // Bitta qatorga 2 ta tugma: Joylashuv va Bosh menyuga qaytish
        KeyboardRow row = new KeyboardRow();

        KeyboardButton locationButton = new KeyboardButton();

        KeyboardButton backButton = new KeyboardButton();
        backButton.setText(i18n.get(Utils.i18n.BUTTON_MAIN_MENU, langCode)); // 🔙 Bosh menyuga qaytish

        locationButton.setText(i18n.get(Utils.i18n.BUTTON_SEND_LOCATION, langCode)); // 📍 Joylashuvni yuborish
        locationButton.setRequestLocation(true);

        row.add(backButton);
        row.add(locationButton);

        keyboardRows.add(row);
        replyKeyboardMarkup.setKeyboard(keyboardRows);

        return replyKeyboardMarkup;
    }

    private ReplyKeyboardMarkup createBaseReplyKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        return replyKeyboardMarkup;
    }
}
