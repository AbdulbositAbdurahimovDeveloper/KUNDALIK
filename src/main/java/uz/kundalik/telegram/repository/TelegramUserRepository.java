package uz.kundalik.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.kundalik.site.exception.EntityNotFoundException;
import uz.kundalik.telegram.model.TelegramUser;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findByChatId(Long chatId);

    default TelegramUser findByChatIdThrow(Long chatId) {
        return this.findByChatId(chatId).orElseThrow(()-> new EntityNotFoundException("Telegram uSer not found with chatId: " + chatId));
    }
}