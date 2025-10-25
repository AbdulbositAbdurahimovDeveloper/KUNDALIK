package uz.kundalik.telegram.service.keybard.user;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import uz.kundalik.telegram.payload.weather.WeatherResponseDTO;
import uz.kundalik.telegram.payload.weather.search.SearchLocationDTO;

import java.util.List;

public interface UserInlineKeyboardService {
    InlineKeyboardMarkup chooseWeatherCity(List<SearchLocationDTO> locationDTOS, String time);

    InlineKeyboardMarkup choosePrayerCity(List<SearchLocationDTO> locationDTOS, String time);

    InlineKeyboardMarkup weatherInfo(WeatherResponseDTO info, String langCode,String  time);

    InlineKeyboardMarkup userChooseWeatherHourly(String infoCity, boolean first, String langCode);

}
