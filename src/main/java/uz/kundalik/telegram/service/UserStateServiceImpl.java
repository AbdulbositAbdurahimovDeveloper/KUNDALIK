package uz.kundalik.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.telegram.enums.UserState;
import uz.kundalik.telegram.model.TelegramUser;
import uz.kundalik.telegram.repository.TelegramUserRepository;

@Service
@RequiredArgsConstructor
public class UserStateServiceImpl implements  UserStateService {

    private final TelegramUserRepository telegramUserRepository;

    @Override
    @Transactional
    public void updateUserState(Long chatId, UserState userState) {
        TelegramUser telegramUser = telegramUserRepository.findByChatIdThrow(chatId);
        telegramUser.setUserState(userState);
        telegramUserRepository.save(telegramUser);
    }
}
