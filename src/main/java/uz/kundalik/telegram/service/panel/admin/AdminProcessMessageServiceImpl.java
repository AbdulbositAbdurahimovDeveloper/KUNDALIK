package uz.kundalik.telegram.service.panel.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class AdminProcessMessageServiceImpl implements  AdminProcessMessageService {
    @Override
    public void processMessage(Message message) {

    }
}
