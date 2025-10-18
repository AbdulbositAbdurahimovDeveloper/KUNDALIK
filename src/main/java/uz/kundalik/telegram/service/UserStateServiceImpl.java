package uz.kundalik.telegram.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.kundalik.telegram.enums.UserState;

@Service
@RequiredArgsConstructor
public class UserStateServiceImpl implements  UserStateService {

    @Override
    public void updateUserState(Long chatId, UserState userState) {

    }
}
