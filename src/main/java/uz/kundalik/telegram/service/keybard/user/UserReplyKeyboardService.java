package uz.kundalik.telegram.service.keybard.user;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import uz.kundalik.telegram.enums.UserStatus;

public interface UserReplyKeyboardService {
    ReplyKeyboardMarkup welcomeMsg(String langCode, UserStatus userStatus);

    ReplyKeyboardMarkup getLocationMenu(String langCode);
}
