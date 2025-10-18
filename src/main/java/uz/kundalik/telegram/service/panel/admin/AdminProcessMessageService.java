package uz.kundalik.telegram.service.panel.admin;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface AdminProcessMessageService {
    void processMessage(Message message);
}
