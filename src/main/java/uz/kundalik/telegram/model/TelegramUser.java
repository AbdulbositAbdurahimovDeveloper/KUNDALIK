package uz.kundalik.telegram.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.kundalik.site.model.User;
import uz.kundalik.telegram.enums.UserState;

@Getter
@Setter
@Entity
@Table(name = "telegram_user")
public class TelegramUser {

    @Id
    private Long chatId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User siteUser;

    @Enumerated(EnumType.STRING)
    private UserState userState = UserState.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "language_id")
    private Language selectedLanguage;

}