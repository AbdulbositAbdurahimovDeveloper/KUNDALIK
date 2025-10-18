package uz.kundalik.telegram.service.panel.user;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserProcessMessageService {
    void processMessage(Message message);
}
