package uz.kundalik.telegram.service;

import uz.kundalik.telegram.enums.UserState;

public interface UserStateService {

    void updateUserState(Long chatId, UserState userState);

}
