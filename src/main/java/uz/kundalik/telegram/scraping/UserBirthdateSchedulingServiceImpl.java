package uz.kundalik.telegram.scraping;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.kundalik.site.model.User;
import uz.kundalik.site.model.UserProfile;
import uz.kundalik.site.payload.TgNotificationUserBirthData;
import uz.kundalik.site.repository.UserProfileRepository;
import uz.kundalik.site.repository.UserRepository;
import uz.kundalik.telegram.model.TelegramUser;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBirthdateSchedulingServiceImpl implements UserBirthdateSchedulingService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final ApplicationEventPublisher publisher;


    @Transactional(readOnly = true)
//    @Scheduled(fixedRate = 10000)
    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Tashkent")
    public void getFoundBirthdays() {
        List<User> allUser = userRepository.findAll();

        allUser.forEach(u -> {
            if (u.getProfile() != null && u.getTelegramUser() != null) {
                UserProfile userProfile = u.getProfile();
                TelegramUser telegramUser = u.getTelegramUser();
                publisher.publishEvent(new TgNotificationUserBirthData(
                                u.getId(),
                                userProfile.getFirstName(),
                                userProfile.getLastName(),
                                userProfile.getBirthDate(),
                                telegramUser.getChatId(),
                                telegramUser.getUserStatus(),
                                telegramUser.getSelectedLanguage() != null ? telegramUser.getSelectedLanguage().getCode() : "uz"
                        )
                );
            }
        });


//        allByBirthDate.forEach(userProfile -> {
//            User user = userProfile.getUser();
//            if (user.getTelegramUser() != null) {
//
//                TelegramUser telegramUser = user.getTelegramUser();
//                publisher.publishEvent(new TgNotificationUserBirthData(
//                                user.getId(),
//                                userProfile.getFirstName(),
//                                userProfile.getLastName(),
//                                userProfile.getBirthDate(),
//                                telegramUser.getChatId(),
//                                telegramUser.getUserStatus(),
//                                telegramUser.getSelectedLanguage() != null ? telegramUser.getSelectedLanguage().getCode() : "uz"
//                        )
//                );
//
//            }
//        });
    }

}
