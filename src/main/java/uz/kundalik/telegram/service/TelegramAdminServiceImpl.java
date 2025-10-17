package uz.kundalik.telegram.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class TelegramAdminServiceImpl implements TelegramAdminService {

    @Override
    public void onUpdateResave(Update update) {
    }
}
