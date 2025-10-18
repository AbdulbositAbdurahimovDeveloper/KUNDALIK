package uz.kundalik.telegram.service.panel.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class UserProcessMessageServiceImpl implements  UserProcessMessageService {
    @Override
    public void processMessage(Message message) {

    }
}
