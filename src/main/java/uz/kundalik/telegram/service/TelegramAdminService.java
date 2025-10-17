package uz.kundalik.telegram.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramAdminService {
    void onUpdateResave(Update update);
}
