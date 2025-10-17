package uz.kundalik.telegram.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.kundalik.telegram.model.TelegramUser;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<TelegramUser> findByChatId(Long chatId);
}