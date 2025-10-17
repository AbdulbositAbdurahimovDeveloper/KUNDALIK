package uz.kundalik.telegram.service.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public interface SendMsg {
//    SendMessage sendMessage(Long userChatId, String sendMessage, InlineKeyboardMarkup inlineKeyboardMarkup);

    SendMessage sendMessage(Long chatId, String message, ReplyKeyboard keyboard);
}
