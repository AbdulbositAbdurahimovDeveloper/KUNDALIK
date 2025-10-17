package uz.kundalik.telegram.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import uz.kundalik.telegram.enums.UserState;

@Service
public class TelegramUserServiceImpl implements TelegramUserService{

    @Override
    public void updateUserState(Long chatId, UserState userState) {

    }

    @Override
    public void onUpdateResave(Update update) {

    }
}
