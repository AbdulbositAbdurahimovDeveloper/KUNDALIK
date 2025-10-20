package uz.kundalik.telegram.service.keybard.user;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;

import java.util.List;

public interface UserInlineKeyboardService {
    InlineKeyboardMarkup chooseCity(List<SearchLocationDTO> locationDTOS);
}
