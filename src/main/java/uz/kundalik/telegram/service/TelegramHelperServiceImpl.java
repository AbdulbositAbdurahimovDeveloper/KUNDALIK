package uz.kundalik.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.site.model.User;
import uz.kundalik.telegram.enums.UserState;
import uz.kundalik.telegram.enums.UserStatus;
import uz.kundalik.telegram.model.Language;
import uz.kundalik.telegram.model.TelegramUser;
import uz.kundalik.telegram.repository.LanguageRepository;
import uz.kundalik.telegram.repository.TelegramUserRepository;

@Service
@RequiredArgsConstructor
public class TelegramHelperServiceImpl implements TelegramHelperService {

    private final TelegramUserRepository telegramUserRepository;
    private final LanguageRepository languageRepository;

    @Transactional(readOnly = true)
    @Override
    public TelegramUser telegramUser(Long chatId) {
        return telegramUserRepository.findByChatIdThrow(chatId);
    }

    @Transactional(readOnly = true)
    @Override
    public Language userLanguage(Long chatId) {
        TelegramUser telegramUser = telegramUserRepository.findByChatIdThrow(chatId);
        if (telegramUser.getSelectedLanguage() != null) {
            return telegramUser.getSelectedLanguage();
        } else {
            return languageRepository.findByDefaultLanguageTrueOrElseThrow();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public String langCode(Long chatId) {
        TelegramUser telegramUser = telegramUserRepository.findByChatIdThrow(chatId);
        if (telegramUser.getSelectedLanguage() != null) {
            return telegramUser.getSelectedLanguage().getCode();
        } else {
            return languageRepository.findByDefaultLanguageTrueOrElseThrow().getCode();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserState userState(Long chatId) {
        return telegramUserRepository.findByChatIdThrow(chatId).getUserState();
    }

    @Transactional(readOnly = true)
    @Override
    public User user(Long chatId) {
        TelegramUser telegramUser = telegramUserRepository.findByChatIdThrow(chatId);
        if (telegramUser.getSelectedLanguage() != null) {
            return telegramUser.getSiteUser();
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserStatus userStatus(Long chatId) {
        return telegramUserRepository.findByChatIdThrow(chatId).getUserStatus();
    }


}
