package uz.kundalik.site.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.kundalik.telegram.enums.UserStatus;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TgNotificationUserBirthData {

    private Long userId;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private Long telegramUserId;
    private UserStatus userStatus;
    private String langCode = "uz";
}
